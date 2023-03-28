package org.CornFarmerNZ.service;

import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.model.Item;
import org.CornFarmerNZ.model.Url;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class MainService {

	@Autowired
	ParsingService parsingService;

	@Autowired
	FileWritingService fileWritingService;

	@Autowired
	DynamoDbEnhancedClient enhancedClient;

	@Autowired
	ChromeOptions chromeOptions;

	private static RemoteWebDriver driver;
	private static final String DRIVER_URL = "http://localhost:4444";

	@Scheduled(cron = "0 0 12 * * *", zone = "Pacific/Auckland")
	public void start() throws InterruptedException {
		try {
			driver = new RemoteWebDriver(new URL(DRIVER_URL), chromeOptions);
		} catch (Exception e) {
			log.error("Error creating remote web driver for url: " + DRIVER_URL, e);
		}
		DynamoDbTable<Url> urlTable = enhancedClient.table("websites_scrape", TableSchema.fromBean(Url.class));
		long itemCount = urlTable
				.describeTable()
				.table()
				.itemCount();
		List<Url> urlList = new ArrayList<>();
		urlTable
				.scan()
				.stream()
				.forEach(urls -> {
					urlList.addAll(urls
							.items()
							.stream()
							.toList());
				});
		List<Item> newItems = new ArrayList<>();
		urlList
				.stream()
				.forEach(url -> {
					try {
						if (!StringUtils.equals("test", url.getUrl())) {
							newItems.addAll(parsingService.getItems(List.of(url.getUrl()),
									url
											.getStore()
											.toString(), driver));
						}
					} catch (InterruptedException e) {
						log.error("Error getting item", e);
					}
				});
		

		DynamoDbTable<Item> itemTable = enhancedClient.table("clothes_scraper", TableSchema.fromBean(Item.class));

		try {
			newItems.removeIf(item -> {
				return StringUtils.isBlank(item.getName());
			});

			newItems.forEach(item -> {
				item.setId(UUID
						.nameUUIDFromBytes(item
								.getUrl()
								.getBytes())
						.toString());
			});

			List<Item> oldItems =
					newItems
							.stream()
							.map(item -> {
								return
										itemTable.getItem(Key
												.builder()
												.partitionValue(item.getId())
												.build());
							})
							.toList();
			for (Item newItem : newItems) {
				for (Item oldItem : oldItems) {
					if (newItem.equals(oldItem)) {
						newItem
								.getPrices()
								.putAll(oldItem.getPrices());
					}
				}
			}
			newItems.forEach(itemTable::putItem);
			log.info("Items added!");
		} catch (Exception e) {
			log.error("Error adding item");
		}
	}

}
