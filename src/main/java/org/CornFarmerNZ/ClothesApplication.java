package org.CornFarmerNZ;


import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.config.AppConfig;
import org.CornFarmerNZ.config.ClothesConfig;
import org.CornFarmerNZ.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;


@Log4j2
@EnableConfigurationProperties({AppConfig.class})
@SpringBootApplication
public class ClothesApplication implements CommandLineRunner {

	@Resource
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private MainService mainService;


	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext =
				new SpringApplicationBuilder(ClothesApplication.class)
						.sources(ClothesConfig.class)
						.web(WebApplicationType.NONE)
						.run();

		Environment env = applicationContext.getEnvironment();

		log.info("Application: '{}' is running!\n", env.getProperty("spring.application.name"));
	}

	@Override
	public void run(String... args) throws Exception {
		applicationContext.start();
		mainService.run();
	}

}

