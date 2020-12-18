# Scorecard application

This application gathers stats on GitHub issues for a particular project by performing [search queries](https://docs.github.com/en/free-pro-team@latest/github/searching-for-information-on-github/searching-issues-and-pull-requests#search-by-milestone)
on the [Github GraphQL API](https://docs.github.com/en/free-pro-team@latest/graphql).

## Getting started

First, clone this repository. You can then edit the `src/main/resources/application.yml` config file and adapt it to your project.
If your project follows Spring Framework / Spring Boot conventions, changing `scorecard.project.name` and `scorecard.project.members` should be enough.
Otherwise, feel free to change the configuration keys related to issues labels.

Now, you're ready to build the app using `./gradlew build`.


[A GitHub application token](https://github.com/settings/tokens) is required for this app. A simple token with no specific OAuth scope should work.
You can use it as an env variable when running the application, here for the month of September 2020:

```
SCORECARD_GITHUB_TOKEN=mytoken java -jar build/libs/scorecard-0.0.1-SNAPSHOT.jar --start=2020-09-01 --end=2020-09-30
```

You should get the ouput on the console:

```
Stats for: spring-projects/spring-boot 2020-11-01 -> 2020-11-30
Team members: ...
Team bots: ...
Assignable Users: ...
Team created: 166
Community created (Inbound Volume): 142
Closed as Duplicates: 16
Closed as Questions: 7
Closed as Declined: 41
Closed as Enhancements: 5
Closed as Back/Forward-port: 71
Closed as Bug/Regression: 54
Closed as Task/Dependency Upgrade: 88
Closed as Documentation: 41
```