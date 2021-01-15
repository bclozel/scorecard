package io.spring.team.scorecard.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mutable stats accumulator
 *
 * @author Simon Baslé
 */
public class Stats {

	private long teamCreated;
	private long communityCreated;
	private long closedAsDuplicates;
	private long closedAsQuestions;
	private long closedAsDeclined;
	private long closedAsEnhancements;
	private long closedAsPort;
	private long closedAsBug;
	private long closedAsTask;
	private long closedAsDocumentation;

	public long getTeamCreated() {
		return teamCreated;
	}

	public Stats setTeamCreated(long teamCreated) {
		this.teamCreated = teamCreated;
		return this;
	}

	public long getCommunityCreated() {
		return communityCreated;
	}

	public Stats setCommunityCreated(long communityCreated) {
		this.communityCreated = communityCreated;
		return this;
	}

	public long getClosedAsDuplicates() {
		return closedAsDuplicates;
	}

	public Stats setClosedAsDuplicates(long closedAsDuplicates) {
		this.closedAsDuplicates = closedAsDuplicates;
		return this;
	}

	public long getClosedAsQuestions() {
		return closedAsQuestions;
	}

	public Stats setClosedAsQuestions(long closedAsQuestions) {
		this.closedAsQuestions = closedAsQuestions;
		return this;
	}

	public long getClosedAsDeclined() {
		return closedAsDeclined;
	}

	public Stats setClosedAsDeclined(long closedAsDeclined) {
		this.closedAsDeclined = closedAsDeclined;
		return this;
	}

	public long getClosedAsEnhancements() {
		return closedAsEnhancements;
	}

	public Stats setClosedAsEnhancements(long closedAsEnhancements) {
		this.closedAsEnhancements = closedAsEnhancements;
		return this;
	}

	public long getClosedAsPort() {
		return closedAsPort;
	}

	public Stats setClosedAsPort(long closedAsPort) {
		this.closedAsPort = closedAsPort;
		return this;
	}

	public long getClosedAsBug() {
		return closedAsBug;
	}

	public Stats setClosedAsBug(long closedAsBug) {
		this.closedAsBug = closedAsBug;
		return this;
	}

	public long getClosedAsTask() {
		return closedAsTask;
	}

	public Stats setClosedAsTask(long closedAsTask) {
		this.closedAsTask = closedAsTask;
		return this;
	}

	public long getClosedAsDocumentation() {
		return closedAsDocumentation;
	}

	public Stats setClosedAsDocumentation(long closedAsDocumentation) {
		this.closedAsDocumentation = closedAsDocumentation;
		return this;
	}

	public List<String> checkInconsistencies(Stats stats) {
		List<String> inconsistencies = new ArrayList<>();
		long thisValue = this.getTeamCreated();
		long otherValue = stats.getTeamCreated();
		if (thisValue != otherValue) {
			inconsistencies.add("teamCreated(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getCommunityCreated();
		otherValue = stats.getCommunityCreated();
		if (thisValue != otherValue) {
			inconsistencies.add("communityCreated(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsDuplicates();
		otherValue = stats.getClosedAsDuplicates();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsDuplicates(" + thisValue + " vs " + otherValue + ")");
		}

				thisValue = this.getClosedAsQuestions();
		otherValue = stats.getClosedAsQuestions();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsQuestions(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsDeclined();
		otherValue = stats.getClosedAsDeclined();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsDeclined(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsEnhancements();
		otherValue = stats.getClosedAsEnhancements();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsEnhancements(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsPort();
		otherValue = stats.getClosedAsPort();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsPort(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsBug();
		otherValue = stats.getClosedAsBug();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsBug(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsTask();
		otherValue = stats.getClosedAsTask();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsTask(" + thisValue + " vs " + otherValue + ")");
		}

		thisValue = this.getClosedAsDocumentation();
		otherValue = stats.getClosedAsDocumentation();
		if (thisValue != otherValue) {
			inconsistencies.add("closedAsDocumentation(" + thisValue + " vs " + otherValue + ")");
		}

		return inconsistencies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Stats stats = (Stats) o;
		return getTeamCreated() == stats.getTeamCreated() &&
				getCommunityCreated() == stats.getCommunityCreated() &&
				getClosedAsDuplicates() == stats.getClosedAsDuplicates() &&
				getClosedAsQuestions() == stats.getClosedAsQuestions() &&
				getClosedAsDeclined() == stats.getClosedAsDeclined() &&
				getClosedAsEnhancements() == stats.getClosedAsEnhancements() &&
				getClosedAsPort() == stats.getClosedAsPort() &&
				getClosedAsBug() == stats.getClosedAsBug() &&
				getClosedAsTask() == stats.getClosedAsTask() &&
				getClosedAsDocumentation() == stats.getClosedAsDocumentation();
	}

	@Override
	public int hashCode() {
		return Objects
				.hash(getTeamCreated(), getCommunityCreated(), getClosedAsDuplicates(), getClosedAsQuestions(), getClosedAsDeclined(), getClosedAsEnhancements(), getClosedAsPort(), getClosedAsBug(), getClosedAsTask(), getClosedAsDocumentation());
	}
}
