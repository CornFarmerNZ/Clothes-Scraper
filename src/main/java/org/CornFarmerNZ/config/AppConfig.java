package org.CornFarmerNZ.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

	@Value("${dynamodb.table}")
	private String table;


}
