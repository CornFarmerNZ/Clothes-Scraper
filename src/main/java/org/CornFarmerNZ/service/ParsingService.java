package org.CornFarmerNZ.service;

import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.model.Item;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@Log4j2
public class ParsingService {

	@Autowired
	Random random;

	@Autowired
	ChromeOptions chromeOptions;

	@Autowired
	HttpClient httpClient;

	private static RemoteWebDriver driver;

	@PostConstruct
	private void init() {
		driver = new RemoteWebDriver(chromeOptions);
	}


	public List<Item> getItems(List<String> urls, String strategy) throws InterruptedException {
		List<Item> items = new ArrayList<>();
		if (StringUtils.equals("KMART", strategy.toUpperCase())) {
			items.addAll(strategyKmart(urls));
		} else if (StringUtils.equals("THE_WAREHOUSE", strategy.toUpperCase())) {
			items.addAll(strategyTheWarehouse(urls));
		} else if (StringUtils.equals("GLASSONS", strategy.toUpperCase())) {
			items.addAll(strategyGlassons(urls));
		} else if (StringUtils.equals("CHEMIST_WAREHOUSE", strategy.toUpperCase())) {
			items.addAll(strategyChemistWarehouse(urls));
		} else if (StringUtils.equals("POSTIE", strategy.toUpperCase())) {
			items.addAll(strategyPostie(urls));
		}
		return items;
	}

	private List<Item> strategyPostie(List<String> urls) throws InterruptedException {
		return chromeDriverGetItem(".product-header__heading", ".product-header__price.price", "", urls);
	}

	private List<Item> strategyKmart(List<String> urls) throws InterruptedException {
		return chromeDriverGetItem(".sc-bdvvtL.cTxaVe.product-title", ".sc-bdvvtL.bjrtyU.product-price-large", "",
				urls);
	}

	private List<Item> strategyTheWarehouse(List<String> urls) {
		return httpRequestGetItemTheWarehouse(urls, "main-product-detail container product-detail product-wrapper");
	}

	private List<Item> strategyGlassons(List<String> urls) throws InterruptedException {
		return chromeDriverGetItem(".product-summary__heading", ".product-summary__price.s-price",
				".lazyautosizes.lazyloaded", urls);
	}

	private List<Item> strategyChemistWarehouse(List<String> urls) {
		return httpRequestGetItemChemistWarehouse(urls);
	}


	private List<Item> httpRequestGetItemTheWarehouse(List<String> urls, String elementClass) {
		List<Item> items = new ArrayList<>();
		HttpRequest httpRequest;
		String[] headers = new String[]{"user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" +
				" (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36", "referrer", urls.get(0).substring(0,
				urls
						.get(0)
						.indexOf("z/") + 1)};

		for (String url : urls) {
			try {
				httpRequest = HttpRequest
						.newBuilder(new URI(url))
						.GET()
						.headers(headers)
						.build();
				Document doc = Jsoup.parse(httpClient
						.send(httpRequest, HttpResponse.BodyHandlers.ofString())
						.body());
				Elements elements = doc.getElementsByClass(elementClass);
				if (!CollectionUtils.isEmpty(elements)) {
					String price = elements
							.get(0)
							.attr("price")
							.substring(elements
									.get(0)
									.attr("price")
									.lastIndexOf("$") + 1);
					String name = elements
							.get(0)
							.attr("product-name");
					Item item = new Item();
					item.setPrice(price);
					item.setName(name);
					item.setUrl(url);
					items.add(item);
				}

			} catch (URISyntaxException | InterruptedException | IOException use) {
				log.error(use);
			}
		}
		return items;
	}


	private List<Item> httpRequestGetItemChemistWarehouse(List<String> urls) {
		List<Item> items = new ArrayList<>();
		HttpRequest httpRequest;
		String[] headers = new String[]{"user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36" +
				" (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36", "referrer", urls.get(0).substring(0,
				urls
						.get(0)
						.indexOf("z/") + 1)};

		for (String url : urls) {
			try {
				httpRequest = HttpRequest
						.newBuilder(new URI(url))
						.GET()
						.headers(headers)
						.build();
				Document doc = Jsoup.parse(httpClient
						.send(httpRequest, HttpResponse.BodyHandlers.ofString())
						.body());
				Item item = new Item();
				String name = doc
						.getElementsByTag("title")
						.get(0)
						.text()
						.replaceAll("Buy ", "")
						.replaceAll(" Online at Chemist Warehouse®", "");
				item.setName(name);
				item.setUrl(url);
				Elements priceElements = doc.getElementsByClass("pres_price_tag");
				if (!CollectionUtils.isEmpty(priceElements)) {
					String price = priceElements
							.get(0)
							.text()
							.substring(priceElements
									.get(0)
									.text()
									.lastIndexOf("$") + 1);
					item.setPrice(price);
				}
				items.add(item);
			} catch (URISyntaxException | InterruptedException | IOException use) {
				log.error(use);
			}
		}
		return items;
	}

	private List<Item> chromeDriverGetItem(String cssTitle, String cssPrice, String cssImage,
										   List<String> urls) throws InterruptedException {

		List<Item> items = new ArrayList<>();
//		System.setProperty("webdriver.chrome.driver", "./chromedriver");

//		ChromeDriverService service = ChromeDriverService.createDefaultService();

		for (String url : urls) {
			Item item = new Item();
			driver.get(url);
			Thread.sleep(random.nextInt(1502, 3507));
			List<WebElement> title = new ArrayList<>(driver.findElements(By.cssSelector(cssTitle)));
			List<WebElement> price = new ArrayList<>(driver.findElements(By.cssSelector(cssPrice)));
			List<WebElement> images = StringUtils.isNotBlank(cssImage) ?
					new ArrayList<>(driver.findElements(By.cssSelector(cssImage))) : new ArrayList<>();
			if (!CollectionUtils.isEmpty(title)) {
				item.setName(StringUtils.isNotBlank(title
						.get(0)
						.getText()) ? title
						.get(0)
						.getText() : "n/a");
			}
			if (!CollectionUtils.isEmpty(price)) {
				item.setPrice(StringUtils.isNotBlank(price
						.get(0)
						.getText()
						.substring(1)) ? price
						.get(0)
						.getText()
						.substring(price
								.get(0)
								.getText()
								.lastIndexOf("$") + 1) : "0.00");
			}
			if (!CollectionUtils.isEmpty(images)) {
				item.setItemImageUrl(images
						.get(0)
						.getAttribute("src"));
			}
			item.setUrl(url);
			System.out.println(item.toString());
			items.add(item);
			Thread.sleep(random.nextInt(1402, 1505));
		}
		return items;
	}


}
