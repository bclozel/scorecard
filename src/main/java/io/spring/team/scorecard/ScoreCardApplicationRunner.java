package io.spring.team.scorecard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.spring.team.scorecard.data.Stats;
import io.spring.team.scorecard.stats.StatsService;
import io.spring.team.scorecard.stats.StatsService2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class ScoreCardApplicationRunner implements ApplicationRunner {

	private static Log logger = LogFactory.getLog(ScoreCardApplicationRunner.class);

	private final StatsService statsServiceDedicatedQuery;
	private final StatsService2 statsServiceInMemory;

	private final ScorecardProperties properties;

	public ScoreCardApplicationRunner(StatsService statsServiceDedicatedQuery, StatsService2 statsServiceInMemory, ScorecardProperties properties) {
		this.statsServiceDedicatedQuery = statsServiceDedicatedQuery;
		this.statsServiceInMemory = statsServiceInMemory;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LocalDate start = parseDate("start", args.getOptionValues("start"));
		LocalDate end = parseDate("end", args.getOptionValues("end"));
		logger.info("Stats for: " + this.properties.getProject().getRepository() + " " + start.toString() + " -> " + end.toString());

		logger.info("Team members: " + StringUtils.collectionToCommaDelimitedString(this.properties.getProject().getMembers()));
		logger.info("Team bots: " + StringUtils.collectionToCommaDelimitedString(this.properties.getProject().getBots()));
		List<String> assignableUsers = this.statsServiceDedicatedQuery.findAssignableUsers().collectList().block();
		logger.info("Assignable Users: " + StringUtils.collectionToCommaDelimitedString(assignableUsers));

		Mono<Stats> singleQuery = Mono.just(new Stats())
				.flatMap(stats -> this.statsServiceInMemory.findAllIssuesCreatedBetween(start, end)
						.collectList()
						.doOnNext(createdIssues -> logger.trace("Found " + createdIssues.size() + " created issues in the period: " + createdIssues.stream().map(i -> "#" + i.id).collect(Collectors.joining(", "))))
						.map(createdIssues -> {
							stats.setTeamCreated(this.statsServiceInMemory.teamCreated(createdIssues, this.properties.getProject().getMembers()));
							stats.setCommunityCreated(this.statsServiceInMemory.calculateInboundVolume(createdIssues, this.properties.getProject().getMembers(), this.properties.getProject().getBots()));
							return stats;
						})
				)
				.flatMap(stats -> this.statsServiceInMemory.findAllIssuesClosedBetween(start, end)
						.collectList()
						.doOnNext(closedIssues -> logger.trace("Found " + closedIssues.size() + " issues in the period: " + closedIssues.stream().map(i -> "#" + i.id).collect(Collectors.joining(", "))))
						.map(closedIssues -> {
							stats.setClosedAsDuplicates(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getDuplicates()));
							stats.setClosedAsQuestions(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getQuestions()));
							stats.setClosedAsDeclined(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getRejected()));
							stats.setClosedAsEnhancements(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getEnhancements()));
							stats.setClosedAsPort(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getPorts()));
							stats.setClosedAsBug(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getBugs()));
							stats.setClosedAsTask( this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getTasks()));
							stats.setClosedAsDocumentation(this.statsServiceInMemory.calculateOutputVolumeByType(closedIssues, this.properties.getLabels().getDocs()));
							return stats;
						})
				);

		Mono<Stats> fanIn = Mono.just(new Stats())
				.flatMap(stats -> this.statsServiceDedicatedQuery.teamCreated(start, end, this.properties.getProject().getMembers())
						.map(stats::setTeamCreated))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateInboundVolume(start, end, this.properties.getProject().getMembers(),
						this.properties.getProject().getBots())
						.map(stats::setCommunityCreated))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getDuplicates())
						.map(stats::setClosedAsDuplicates))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getQuestions())
						.map(stats::setClosedAsQuestions))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getRejected())
						.map(stats::setClosedAsDeclined))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getEnhancements())
						.map(stats::setClosedAsEnhancements))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getPorts())
						.map(stats::setClosedAsPort))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getBugs())
						.map(stats::setClosedAsBug))
				.flatMap(stats ->  this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getTasks())
						.map(stats::setClosedAsTask))
				.flatMap(stats -> this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getDocs())
						.map(stats::setClosedAsDocumentation));

		final Tuple2<Stats, Stats> bothStats = Mono.zip(singleQuery, fanIn).block();
		Stats singleQueryStats = bothStats.getT1();
		Stats fanInStats = bothStats.getT2();

		final List<String> inconsistencies = fanInStats.checkInconsistencies(singleQueryStats);
		if (!inconsistencies.isEmpty()) {
			logger.warn("Inconsistencies found between two methods: " + String.join(", ", inconsistencies));
		}

		logger.info("Team created: " + singleQueryStats.getTeamCreated());
		logger.info("Community created (Inbound Volume): " + singleQueryStats.getCommunityCreated());
		logger.info("Closed as Duplicates: " + singleQueryStats.getClosedAsDuplicates());
		logger.info("Closed as Questions: " + singleQueryStats.getClosedAsQuestions());
		logger.info("Closed as Declined: " + singleQueryStats.getClosedAsDeclined());
		logger.info("Closed as Enhancements: " + singleQueryStats.getClosedAsEnhancements());
		logger.info("Closed as Back/Forward-port: " + singleQueryStats.getClosedAsPort());
		logger.info("Closed as Bug/Regression: " + singleQueryStats.getClosedAsBug());
		logger.info("Closed as Task/Dependency Upgrade: " + singleQueryStats.getClosedAsTask());
		logger.info("Closed as Documentation: " + singleQueryStats.getClosedAsDocumentation());
	}

	private LocalDate parseDate(String name, List<String> argument) {
		Assert.state(argument != null && StringUtils.hasText(argument.get(0)),
				"Argument --" + name + " should be defined");
		return LocalDate.parse(argument.get(0), DateTimeFormatter.ISO_DATE);
	}
}
