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
Stats for: spring-projects/spring-boot 2020-09-01 -> 2020-09-30
Team members: ...
Team bots: ...
Assignable Users: ...
Inbound Volume: 118
Rejections: 63
Adjusted Inbound Volume: 315
Output Volume: 193
Output Volume (enhancements): 32
Output Volume (bugs): 51
Output Volume (tasks): 206
Output Volume (docs): 28
```