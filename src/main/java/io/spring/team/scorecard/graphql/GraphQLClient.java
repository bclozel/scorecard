package io.spring.team.scorecard.graphql;

import java.io.IOException;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import io.spring.team.scorecard.AssignableUsersQuery;
import io.spring.team.scorecard.IssueCountQuery;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GraphQLClient {

	private final Log logger = LogFactory.getLog(GraphQLClient.class);

	private final ApolloClient apolloClient;

	public GraphQLClient(String githubToken) {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.addInterceptor(new AuthorizationInterceptor(githubToken));
		this.apolloClient = ApolloClient.builder()
				.serverUrl("https://api.github.com/graphql")
				.okHttpClient(clientBuilder.build())
				.build();
	}


	public Mono<Integer> searchNumberOfIssuesAndPRs(String searchQuery) {
		logger.debug("query: " + searchQuery);
		return Mono.create(sink -> {
			this.apolloClient.query(new IssueCountQuery(searchQuery))
					.enqueue(new ApolloCall.Callback<IssueCountQuery.Data>() {
						@Override
						public void onResponse(@NotNull Response<IssueCountQuery.Data> response) {
							sink.success(response.getData().search().issueCount());
						}

						@Override
						public void onFailure(@NotNull ApolloException e) {
							sink.error(e);
						}
					});
		});
	}

	public Flux<String> findAssignableUsers(String org, String repo) {
		return Flux.create(sink -> {
			this.apolloClient.query(new AssignableUsersQuery(org, repo))
					.enqueue(new ApolloCall.Callback<AssignableUsersQuery.Data>() {
						@Override
						public void onResponse(@NotNull Response<AssignableUsersQuery.Data> response) {
							response.getData().repository().assignableUsers()
									.nodes().forEach(node -> sink.next(node.login()));
							sink.complete();
						}

						@Override
						public void onFailure(@NotNull ApolloException e) {
							sink.error(e);
						}
					});
		});
	}

	private static class AuthorizationInterceptor implements Interceptor {

		private final String token;

		public AuthorizationInterceptor(String token) {
			this.token = token;
		}

		@Override
		public okhttp3.Response intercept(Chain chain) throws IOException {
			Request request = chain.request().newBuilder()
					.addHeader("Authorization", "Bearer " + this.token).build();
			return chain.proceed(request);
		}
	}
}
