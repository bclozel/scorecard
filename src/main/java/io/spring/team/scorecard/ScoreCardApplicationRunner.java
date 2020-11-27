package io.spring.team.scorecard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.spring.team.scorecard.stats.StatsService;
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

	private final StatsService statsService;

	private final ScorecardProperties properties;

	public ScoreCardApplicationRunner(StatsService statsService, ScorecardProperties properties) {
		this.statsService = statsService;
		this.properties = properties;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		LocalDate start = parseDate("start", args.getOptionValues("start"));
		LocalDate end = parseDate("end", args.getOptionValues("end"));
		logger.info("Stats for: " + this.properties.getProject().getRepository() + " " + start.toString() + " -> " + end.toString());

		logger.info("Team members: " + StringUtils.collectionToCommaDelimitedString(this.properties.getProject().getMembers()));
		logger.info("Team bots: " + StringUtils.collectionToCommaDelimitedString(this.properties.getProject().getBots()));
		List<String> assignableUsers = this.statsService.findAssignableUsers().collectList().block();
		logger.info("Assignable Users: " + StringUtils.collectionToCommaDelimitedString(assignableUsers));
		logger.info("Inbound Volume: " + this.statsService.calculateInboundVolume(start, end,
				this.properties.getProject().getMembers(), this.properties.getProject().getBots()).block());
		logger.info("Rejections: " + this.statsService.calculateRejections(start, end,
				this.properties.getLabels().getRejected()));
		logger.info("Adjusted Inbound Volume: " + this.statsService.calculateAdjustedInboundVolume(start, end).block());
		logger.info("Output Volume: " + this.statsService.calculateOutputVolume(start, end, this.properties.getLabels().getPorts()).block());

		logger.info("Output Volume (enhancements): " + this.statsService.calculateOutputVolumeByType(start, end, this.properties.getLabels().getEnhancements()).block());
		logger.info("Output Volume (bugs): " + this.statsService.calculateOutputVolumeByType(start, end, this.properties.getLabels().getBugs()).block());
		logger.info("Output Volume (tasks): " + this.statsService.calculateOutputVolumeByType(start, end, this.properties.getLabels().getTasks()).block());
		logger.info("Output Volume (docs): " + this.statsService.calculateOutputVolumeByType(start, end, this.properties.getLabels().getDocs()).block());
	}


	private LocalDate parseDate(String name, List<String> argument) {
		Assert.state(argument != null && StringUtils.hasText(argument.get(0)),
				"Argument --" + name + " should be defined");
		return LocalDate.parse(argument.get(0), DateTimeFormatter.ISO_DATE);
	}
}
