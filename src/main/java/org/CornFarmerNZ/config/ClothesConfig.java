package org.CornFarmerNZ.config;

import lombok.Data;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Random;

@Configuration
@Data
public class ClothesConfig {

	@Bean
	HttpClient httpClient() {
		return HttpClient
				.newBuilder()
				.build();
	}

	@Bean
	Random random() {
		return new Random();
	}

	@Bean
	ChromeOptions chromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments(List.of("user-agent=\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" +
				" (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36", "--disable-blink-features=AutomationControlled"));
		options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
		return options;
	}


}
