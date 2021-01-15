package io.spring.team.scorecard.stats;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.spring.team.scorecard.data.Issue;
import io.spring.team.scorecard.graphql.GraphQLClient;
import io.spring.team.scorecard.graphql.SearchQueryBuilder;
import reactor.core.publisher.Flux;

/**
 * @author Simon Baslé
 */
public class StatsService2 {

	private final String org;

	private final String repo;

	private final GraphQLClient client;

	public StatsService2(String org, String repo, GraphQLClient client) {
		this.org = org;
		this.repo = repo;
		this.client = client;
	}

	public Flux<Issue> findAllIssuesCreatedBetween(LocalDate start, LocalDate end) {
		String query = SearchQueryBuilder.create(this.org, this.repo)
				.createdBetween(start, end)
				.build();
		return this.client.searchIssuesAndPRs(query);
	}

	public Flux<Issue> findAllIssuesClosedBetween(LocalDate start, LocalDate end) {
		String query = SearchQueryBuilder.create(this.org, this.repo)
				.closedBetween(start, end)
				.build();
		return this.client.searchIssuesAndPRs(query);
	}

	public long teamCreated(List<Issue> issues, List<String> members) {
		return issues.stream().filter(issue -> members.contains(issue.author)).count();
	}

	public long calculateInboundVolume(List<Issue> issues, List<String> members, List<String> bots) {
		return issues.stream().filter(issue -> !members.contains(issue.author) && !bots.contains(issue.author)).count();
	}

	/**
	 * @param closedIssues the list of issues that were closed during the period
	 * @param typeLabelsList the labels to consider (OR)
	 * @return
	 */
	public long calculateOutputVolumeByType(List<Issue> closedIssues, List<String> typeLabelsList) {
		Set<String> typeLabels = new HashSet<>(typeLabelsList);
		return closedIssues.stream()
				.filter(issue -> {
					Set<String> labels = new HashSet<>(issue.labels);
					labels.retainAll(typeLabels);
					return !labels.isEmpty();
				})
				.count();
	}
}
