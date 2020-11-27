package io.spring.team.scorecard.graphql;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;

public class SearchQueryBuilder {

	private final String repository;

	private String created;

	private String closed;

	private String author;

	private Set<String> labels = new HashSet<>();

	private Set<String> ignoringLabels = new HashSet<>();

	private Milestone milestone = Milestone.UNKNOWN;

	private State state = State.UNKNOWN;

	private Type type = Type.UNKNOWN;

	private SearchQueryBuilder(String repository, String created, String closed, String author, Set<String> labels,
			Set<String> ignoringLabels, Milestone milestone, State state, Type type) {
		this.repository = repository;
		this.created = created;
		this.closed = closed;
		this.author = author;
		this.labels.addAll(labels);
		this.ignoringLabels.addAll(ignoringLabels);
		this.milestone = milestone;
		this.state = state;
		this.type = type;
	}

	public static SearchQueryBuilder create(String organization, String repository) {
		return new SearchQueryBuilder(organization + "/" + repository, null, null, null,
				Collections.emptySet(), Collections.emptySet(), Milestone.UNKNOWN, State.UNKNOWN, Type.UNKNOWN);
	}

	public SearchQueryBuilder createdBetween(LocalDate start, LocalDate end) {
		String created = String.format("%s..%s", start.format(DateTimeFormatter.ISO_DATE), end.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder createdBefore(LocalDate date) {
		String created = String.format("<%s", date.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder createdAfter(LocalDate date) {
		String created = String.format(">=%s", date.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder closedBetween(LocalDate start, LocalDate end) {
		String closed = String.format("%s..%s", start.format(DateTimeFormatter.ISO_DATE), end.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, this.created, closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder closedBefore(LocalDate date) {
		String closed = String.format("<%s", date.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, this.created, closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder closedAfter(LocalDate date) {
		String closed = String.format(">=%s", date.format(DateTimeFormatter.ISO_DATE));
		return new SearchQueryBuilder(this.repository, this.created, closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder author(String author) {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder withLabel(String label) {
		this.labels.add(label);
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder ignoringLabel(String label) {
		this.ignoringLabels.add(label);
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder notMilestoned() {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, Milestone.NOT_MILESTONED, this.state, Type.UNKNOWN);
	}

	public SearchQueryBuilder isOpen() {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, State.OPEN, Type.UNKNOWN);
	}

	public SearchQueryBuilder isClosed() {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, State.CLOSED, Type.UNKNOWN);
	}

	public SearchQueryBuilder isIssue() {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.ISSUE);
	}

	public SearchQueryBuilder isPr() {
		return new SearchQueryBuilder(this.repository, this.created, this.closed, this.author, this.labels,
				this.ignoringLabels, this.milestone, this.state, Type.PR);
	}

	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("repo:").append(this.repository).append(" ");
		if (StringUtils.hasText(this.created)) {
			builder.append("created:").append(this.created).append(" ");
		}
		if (StringUtils.hasText(this.closed)) {
			builder.append("closed:").append(this.closed).append(" ");
		}
		if (StringUtils.hasText(this.author)) {
			builder.append("author:").append(this.author).append(" ");
		}
		this.labels.forEach(label -> builder.append("label:\"").append(label).append("\" "));
		this.ignoringLabels.forEach(label -> builder.append("-label:\"").append(label).append("\" "));
		if (this.milestone == Milestone.NOT_MILESTONED) {
			builder.append("no:milestone ");
		}
		switch (this.state) {
			case OPEN:
				builder.append("is:open ");
				break;
			case CLOSED:
				builder.append("is:closed ");
				break;
		}
		switch (this.type) {
			case ISSUE:
				builder.append("is:issue ");
				break;
			case PR:
				builder.append("is:pr ");
				break;
		}
		return builder.toString();
	}

	public enum Milestone {
		NOT_MILESTONED, UNKNOWN
	}

	public enum State {
		CLOSED, OPEN, UNKNOWN
	}

	public enum Type {
		ISSUE, PR, UNKNOWN
	}
}
