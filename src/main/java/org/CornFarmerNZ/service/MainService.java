package org.CornFarmerNZ.service;

import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.constants.Store;
import org.CornFarmerNZ.model.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

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

	public static boolean alreadyRanToday = false;


	List<String> kmartUrls = List.of("https://www.kmart.co.nz/product/basic-t-shirt-s145738/?selectedSwatch=Black",
			"https://www.kmart.co.nz/product/short-sleeve-twist-front-midi-dress-s159474" +
					"/?selectedSwatch=Black",
			"https://www.kmart.co.nz/product/3-pack-opaque-tights-s113359/", "https://www.kmart.co" +
					".nz/product/comfort-sneakers-with-elastic-laces-s141434/?selectedSwatch=Black", "https://www" +
					".kmart.co.nz/product/food-and-fun-icons-mug-43146321/", "https://www.kmart.co" +
					".nz/product/open-leg-trackpants-s156686/", "https://www.kmart.co" +
					".nz/product/stripe-beach-slides-s158550/", "https://www.kmart.com.au/product/plush-novelty-backpack-pink-71211336/");

	List<String> theWarehouseUrls = List.of("https://www.thewarehouse.co.nz/p/hh-womens-flat-ankle-boots/RM110139534" +
			"-1M.html", "https://www.thewarehouse.co.nz/p/hh-mens-crew-neck-short-sleeve-plain-tee/RM110092972-1M" +
			".html", "https://www.thewarehouse.co.nz/p/hh-mens-trackpants/RM110022253-1M.html");

	List<String> glassonsUrls = List.of("https://www.glassons.com/nz/p/wide-leg-cargo-pant-pw50851cot-black", "https" +
			"://www.glassons.com/nz/p/parachute-pant-pw55457nyl-black", "https://www.glassons" +
			".com/nz/p/parachute-pant-pw55457nyl-lunar", "https://www.glassons.com/nz/p/parachute-pant-pw55457nyl" +
			"-romaine-calm", "https://www.glassons.com/nz/p/parachute-pant-pw55457nyl-susan-soil", "https://www" +
			".glassons.com/nz/p/parachute-pant-pw55457nyl-white", "https://www.glassons" +
			".com/nz/p/cargo-parachute-pant-pw91723nyl-black", "https://www.glassons" +
			".com/nz/p/cargo-parachute-pant-pw91723nyl-gun-smoke", "https://www.glassons" +
			".com/nz/p/cargo-parachute-pant-pw91723nyl-when-in-sprout", "https://www.glassons" +
			".com/nz/p/parachute-pant-pw55457nyl-sourdough", "https://www.glassons.com/nz/p/low-rise-cotton-cargo-pant-pw54095cot-black");
	List<String> chemistWarehouseUrls = List.of("https://www.chemistwarehouse.co" +
			".nz/buy/114182/revolution-skincare-2-5-glycolic-acid-tonic-200ml");
	List<String> postieUrls = List.of("https://www.postie.co.nz/womens-midi-t-shirt-dress-816974-black");
	List<String> ippondoUrls = List.of("https://ippondonz.co.nz/?product=cowbrand-additive-free-milky-body-soap-550ml", "https://ippondonz.co.nz/?product=milky-body-soap-gentle-soap-scent-with-pump-550ml");
	List<String> daikokuUrls = List.of("https://www.daikokunz.com/product/biore-u-moisturizing-body-wash/");

	@Scheduled(cron = "0 0 12 * * *", zone = "Pacific/Auckland")
	public void start() throws InterruptedException {
		List<Item> newItems = new ArrayList<>();
		//chrome driver
		newItems.addAll(parsingService.getItems(kmartUrls, Store.KMART.toString()));
		newItems.addAll(parsingService.getItems(glassonsUrls, Store.GLASSONS.toString()));
		newItems.addAll(parsingService.getItems(postieUrls, Store.POSTIE.toString()));
		//api calls
		newItems.addAll(parsingService.getItems(theWarehouseUrls, Store.THE_WAREHOUSE.toString()));
		newItems.addAll(parsingService.getItems(chemistWarehouseUrls, Store.CHEMIST_WAREHOUSE.toString()));
		newItems.addAll(parsingService.getItems(ippondoUrls, Store.IPPONDO.toString()));
		newItems.addAll(parsingService.getItems(daikokuUrls, Store.DAIKOKU.toString()));

		TableSchema<Item> itemTableSchema = TableSchema.fromBean(Item.class);
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
		} catch (Exception e) {
			log.error("Error adding item");
		}
		System.out.println("break");
	}

	public void run() {

	}
}
