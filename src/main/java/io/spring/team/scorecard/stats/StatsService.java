package io.spring.team.scorecard.stats;

import java.time.LocalDate;
import java.util.List;

import io.spring.team.scorecard.graphql.GraphQLClient;
import io.spring.team.scorecard.graphql.SearchQueryBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class StatsService {

	private final String org;

	private final String repo;

	private final GraphQLClient client;

	public StatsService(String org, String repo, GraphQLClient client) {
		this.org = org;
		this.repo = repo;
		this.client = client;
	}

	/**
	 * Calculate the "Inbound Volume" for the given period.
	 * "Inbound Volume" = "all issues created" - "issues created by team members" - "issues created by bots"
	 * Each number is calculated for the issues created during the given period.
	 */
	public Mono<Integer> calculateInboundVolume(LocalDate start, LocalDate end, List<String> membersLogin, List<String> botsLogin) {
		Mono<Integer> totalCreated = findNumberOfCreatedIssues(start, end);
		Mono<Integer> createdByTeam = findNumberOfIssuesCreatedByUsers(start, end, membersLogin);
		Mono<Integer> createdByBots = findNumberOfIssuesCreatedByUsers(start, end, botsLogin);
		return Mono.zip(totalCreated, createdByBots, createdByTeam)
				.map((tuple) -> tuple.getT1() - tuple.getT2() - tuple.getT3());
	}

	/**
	 * Calculate the "Team created issues count" for the given period.
	 */
	public Mono<Integer> teamCreated(LocalDate start, LocalDate end, List<String> membersLogin) {
		return findNumberOfIssuesCreatedByUsers(start, end, membersLogin);
	}

	/**
	 * Calculate the "Rejections" for the given period.
	 * Sum the number of issues closed for each label in the given list.
	 */
	public Mono<Integer> calculateRejections(LocalDate start, LocalDate end, List<String> rejectionLabels) {
		return findNumberOfIssuesClosedWithLabels(start, end, rejectionLabels);
	}

	/**
	 * Calculate the "Adjusted Inbound Volume" for the given period.
	 * "Adjusted Inbound Volume" = "all issues created during the period" - "issues closed without milestone (triaged/discarded)"
	 */
	public Mono<Integer> calculateAdjustedInboundVolume(LocalDate start, LocalDate end) {
		Mono<Integer> totalCreated = this.findNumberOfCreatedIssues(start, end);
		Mono<Integer> closedWithoutMilestone = findNumberOfIssuesCreatedAndClosedWithoutMilestone(start, end);
		return Mono.zip(totalCreated, closedWithoutMilestone)
				.map((tuple) -> tuple.getT1() - tuple.getT2());
	}

	/**
	 * Calculate the "Output Volume" for the given period.
	 * "Output Volume" = "Closed with milestone (fixed)" - "Issues closed as backports and forward-ports"
	 * OR
	 * "Output Volume" = "Closed" - "Closed without milestone" - "Issues closed as backports and forward-ports"
	 */
	public Mono<Integer> calculateOutputVolume(LocalDate start, LocalDate end, List<String> portsLabels) {
		Mono<Integer> totalClosed = this.findNumberOfIssuesCreatedAndClosed(start, end);
		Mono<Integer> closedWithoutMilestone = this.findNumberOfIssuesCreatedAndClosedWithoutMilestone(start, end);
		Mono<Integer> closedAsPorts = this.findNumberOfIssuesClosedWithLabels(start, end, portsLabels);
		return Mono.zip(totalClosed, closedWithoutMilestone, closedAsPorts)
				.map((tuple) -> tuple.getT1() - tuple.getT2() - tuple.getT3());
	}

	/**
	 * Calculate the "Output Volume by Type" for the given period.
	 * "Output Volume for Type 'bugs'" = "Number of issues closed with milestone and tagged with one of the 'bugs'-related tags"
	 */
	public Mono<Integer> calculateOutputVolumeByType(LocalDate start, LocalDate end, List<String> typeLabels) {
		return this.findNumberOfIssuesClosedWithLabels(start, end, typeLabels);
	}

	/**
	 * Number of issues created during the given period.
	 */
	public Mono<Integer> findNumberOfCreatedIssues(LocalDate start, LocalDate end) {
		String query = SearchQueryBuilder.create(this.org, this.repo).createdBetween(start, end).build();
		return this.client.searchNumberOfIssuesAndPRs(query);
	}

	/**
	 * Number of issues created during the period, authored by any of the given user logins.
	 */
	public Mono<Integer> findNumberOfIssuesCreatedByUsers(LocalDate start, LocalDate end, List<String> logins) {
		return Flux.fromIterable(logins)
				.flatMap(login -> {
					String query = SearchQueryBuilder.create(this.org, this.repo)
							.author(login).createdBetween(start, end).build();
					return this.client.searchNumberOfIssuesAndPRs(query);
				}).reduce(Integer::sum);
	}

	/**
	 * Number of issues closed during the period that is tagged by any of the given labels.
	 * Note: the issues might have been created before the given period.
	 */
	public Mono<Integer> findNumberOfIssuesClosedWithLabels(LocalDate start, LocalDate end, List<String> labels) {
		return Flux.fromIterable(labels)
				.flatMap(label -> {
					String query = SearchQueryBuilder.create(this.org, this.repo)
							.withLabel(label).closedBetween(start, end).build();
					return this.client.searchNumberOfIssuesAndPRs(query);
				}).reduce(Integer::sum);
	}

	/**
	 * Number of issues created during the given period, and then closed without milestone during the same period.
	 */
	public Mono<Integer> findNumberOfIssuesCreatedAndClosedWithoutMilestone(LocalDate start, LocalDate end) {
		String query = SearchQueryBuilder.create(this.org, this.repo).createdBetween(start, end)
				.closedBetween(start, end).notMilestoned().build();
		return this.client.searchNumberOfIssuesAndPRs(query);
	}

	/**
	 * Number of issues created but not closed during the given period.
	 * For that, we need to find the number of issues that are still open, or that were closed after the given period.
	 */
	public Mono<Integer> findNumberOfIssuesCreatedAndOpen(LocalDate start, LocalDate end) {
		String stillOpened = SearchQueryBuilder.create(this.org, this.repo).createdBetween(start, end)
				.isOpen().build();
		String closedLater = SearchQueryBuilder.create(this.org, this.repo).createdBetween(start, end)
				.closedAfter(end).build();
		return Mono.zip(this.client.searchNumberOfIssuesAndPRs(stillOpened), this.client.searchNumberOfIssuesAndPRs(closedLater))
				.map(tuple -> tuple.getT1() + tuple.getT2());
	}

	/**
	 * Number of issues created during the given period, and then closed during the same period.
	 */
	public Mono<Integer> findNumberOfIssuesCreatedAndClosed(LocalDate start, LocalDate end) {
		String query = SearchQueryBuilder.create(this.org, this.repo)
				.createdBetween(start, end).closedBetween(start, end).build();
		return this.client.searchNumberOfIssuesAndPRs(query);
	}

	public Flux<String> findAssignableUsers() {
		return this.client.findAssignableUsers(this.org, this.repo);
	}

}
