package io.spring.team.scorecard;

import io.spring.team.scorecard.graphql.GraphQLClient;
import io.spring.team.scorecard.stats.StatsService;
import io.spring.team.scorecard.stats.StatsService2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScoreCardConfig {

	@Bean
	public GraphQLClient graphQLClient(ScorecardProperties properties) {
		return new GraphQLClient(properties.getGithub().getToken());
	}

	@Bean
	public StatsService statsService(ScorecardProperties properties, GraphQLClient graphQLClient) {
		return new StatsService(properties.getProject().getOrg(), properties.getProject().getName(), graphQLClient);
	}

	@Bean
	public StatsService2 statsService2(ScorecardProperties properties, GraphQLClient graphQLClient) {
		return new StatsService2(properties.getProject().getOrg(), properties.getProject().getName(), graphQLClient);
	}
}
