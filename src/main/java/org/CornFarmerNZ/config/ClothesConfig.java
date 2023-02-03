package org.CornFarmerNZ.config;

import lombok.Data;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.util.ArrayList;
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
		String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

		ChromeOptions chromeOptions = new ChromeOptions();
		// User agent is required because some websites will reject your request if it does not have a user agent
		chromeOptions.addArguments(String.format("user-agent=%s", USER_AGENT));
		chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
		chromeOptions.addArguments("--log-level=OFF");
		chromeOptions.setHeadless(true);
//		chromeOptions.setBinary("/usr/bin/);
		List<String> arguments = new ArrayList<>();
		arguments.add("--disable-blink-features=AutomationControlled");
		arguments.add("--disable-extensions");
		arguments.add("--headless");
		arguments.add("--disable-gpu");
		arguments.add("--no-sandbox");
		arguments.add("--incognito");
		arguments.add("--disable-application-cache");
		arguments.add("--disable-dev-shm-usage");
		chromeOptions.addArguments(arguments);
		return chromeOptions;
	}


}
