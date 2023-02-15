package org.CornFarmerNZ.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AwsConfig {
	@Bean
	DynamoDbClient ddb() {
		return DynamoDbClient
				.builder()
				.region(Region.US_WEST_2)
				.build();
	}

	@Bean
	DynamoDbEnhancedClient enhancedClient() {
		return DynamoDbEnhancedClient
				.builder()
				.dynamoDbClient(ddb())
				.build();
	}
}
