package io.spring.team.scorecard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ScorecardProperties.class)
public class ScorecardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScorecardApplication.class, args);
	}

}
