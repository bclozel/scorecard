package io.spring.team.scorecard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.spring.team.scorecard.data.Issue;
import io.spring.team.scorecard.stats.StatsService;
import io.spring.team.scorecard.stats.StatsService2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

		List<Issue> issues = this.statsServiceInMemory.findAllIssuesCreatedBetween(start, end)
				.collectList().block();
		logger.debug("Found " + issues.size() + " issues: " + issues.stream().map(i -> "#" + i.id).collect(Collectors.joining(", ")));

		long teamCreated2 = this.statsServiceInMemory.teamCreated(issues, this.properties.getProject().getMembers());
		long communityCreated2 = this.statsServiceInMemory.calculateInboundVolume(issues, this.properties.getProject().getMembers(), this.properties.getProject().getBots());
		long closedAsDuplicates2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getDuplicates());
		long closedAsQuestions2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getQuestions());
		long closedAsDeclined2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getRejected());
		long closedAsEnhancements2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getEnhancements());
		long closedAsPort2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getPorts());
		long closedAsBug2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getBugs());
		long closedAsTask2 =  this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getTasks());
		long closedAsDocumentation2 = this.statsServiceInMemory.calculateOutputVolumeByType(issues, this.properties.getLabels().getDocs());


		long teamCreatedDedicatedQuery = this.statsServiceDedicatedQuery.teamCreated(start, end, this.properties.getProject().getMembers()).block();
		long communityCreatedDedicatedQuery = this.statsServiceDedicatedQuery.calculateInboundVolume(start, end, this.properties.getProject().getMembers(), this.properties.getProject().getBots()).block();
		long closedAsDuplicatesDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getDuplicates()).block();
		long closedAsQuestionsDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getQuestions()).block();
		long closedAsDeclinedDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getRejected()).block();
		long closedAsEnhancementsDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getEnhancements()).block();
		long closedAsPortDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getPorts()).block();
		long closedAsBugDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getBugs()).block();
		long closedAsTaskDedicatedQuery =  this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getTasks()).block();
		long closedAsDocumentationDedicatedQuery = this.statsServiceDedicatedQuery.calculateOutputVolumeByType(start, end, this.properties.getLabels().getDocs()).block();


		checkConsistent("Team created", teamCreatedDedicatedQuery, teamCreated2);
		checkConsistent("Community created (Inbound Volume)", communityCreatedDedicatedQuery, communityCreated2);
		checkConsistent("Closed as Duplicates", closedAsDuplicatesDedicatedQuery, closedAsDuplicates2);
		checkConsistent("Closed as Questions", closedAsQuestionsDedicatedQuery, closedAsQuestions2);
		checkConsistent("Closed as Declined", closedAsDeclinedDedicatedQuery, closedAsDeclined2);
		checkConsistent("Closed as Enhancements", closedAsEnhancementsDedicatedQuery, closedAsEnhancements2);
		checkConsistent("Closed as Back/Forward-port", closedAsPortDedicatedQuery, closedAsPort2);
		checkConsistent("Closed as Bug/Regression", closedAsBugDedicatedQuery, closedAsBug2);
		checkConsistent("Closed as Task/Dependency Upgrade", closedAsTaskDedicatedQuery, closedAsTask2);
		checkConsistent("Closed as Documentation", closedAsDocumentationDedicatedQuery, closedAsDocumentation2);
	}

	private void checkConsistent(String label, long method1, long method2) {
		if (method1 != method2) {
			logger.warn(label + ": inconsistent data between methods, got " + method1 + " and " + method2);
		}
		logger.info(label + ": " + method1);
	}


	private LocalDate parseDate(String name, List<String> argument) {
		Assert.state(argument != null && StringUtils.hasText(argument.get(0)),
				"Argument --" + name + " should be defined");
		return LocalDate.parse(argument.get(0), DateTimeFormatter.ISO_DATE);
	}
}
