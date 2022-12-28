package org.CornFarmerNZ.service;

import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.constants.Store;
import org.CornFarmerNZ.model.Item;
import org.CornFarmerNZ.repository.ItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MainService {

	@Autowired
	ParsingService parsingService;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	FileWritingService fileWritingService;

	public static boolean alreadyRanToday = false;


	List<String> kmartUrls = List.of("https://www.kmart.co.nz/product/short-sleeve-twist-front-midi-dress-s159474" +
					"/?selectedSwatch=Black", "https://www.kmart.co.nz/product/basic-t-shirt-s145738/?selectedSwatch=Black",
			"https://www.kmart.co.nz/product/3-pack-opaque-tights-s113359/", "https://www.kmart.co" +
					".nz/product/comfort-sneakers-with-elastic-laces-s141434/?selectedSwatch=Black", "https://www" +
					".kmart.co.nz/product/food-and-fun-icons-mug-43146321/", "https://www.kmart.co.nz/product/open-leg-trackpants-s156686/");

	List<String> theWarehouseUrls = List.of("https://www.thewarehouse.co.nz/p/hh-womens-flat-ankle-boots/RM110139534" +
			"-1M.html", "https://www.thewarehouse.co.nz/p/hh-mens-crew-neck-short-sleeve-plain-tee/RM110092972-1M" +
			".html", "https://www.thewarehouse.co.nz/p/hh-mens-trackpants/RM110022253-1M.html");

	List<String> glassonsUrls = List.of("https://www.glassons.com/nz/p/wide-leg-cargo-pant-pw50851cot-black");
	List<String> chemistWarehouseUrls = List.of("https://www.chemistwarehouse.co" +
			".nz/buy/114182/revolution-skincare-2-5-glycolic-acid-tonic-200ml");
	List<String> postieUrls = List.of("https://www.postie.co.nz/womens-midi-t-shirt-dress-816974-black");

	@Scheduled(cron = "* * * 1 * *")
	public void start() throws InterruptedException {
		List<Item> allItems = new ArrayList<>();
		allItems.addAll(parsingService.getItems(kmartUrls, Store.KMART.toString()));
		allItems.addAll(parsingService.getItems(theWarehouseUrls, Store.THE_WAREHOUSE.toString()));
		allItems.addAll(parsingService.getItems(glassonsUrls, Store.GLASSONS.toString()));
		allItems.addAll(parsingService.getItems(chemistWarehouseUrls, Store.CHEMIST_WAREHOUSE.toString()));
		allItems.addAll(parsingService.getItems(postieUrls, Store.POSTIE.toString()));

//		fileWritingService.writeToCsv(allItems);
		log.info(allItems);
		List<Item> oldItems = itemRepository.findAll();
		if (!alreadyRanToday) {
			// adds price of today's items to the existing item in DB.
			for (Item oldItem : oldItems) {
				for (Item newItem : allItems) {
					if (StringUtils.equals(oldItem.getName(), newItem.getName())) {
						oldItem.setPrice(oldItem.getPrice() + "," + newItem.getPrice());
					}
				}
			}
		}
		System.out.println("breakpoint");
		// adds new items to db.
		int counter = 0;
		for (Item newItem : allItems) {
			if (!oldItems.contains(newItem)) {
				oldItems.add(newItem);
				counter++;
			}
		}
		log.info("New items added: " + counter);
		System.out.println("breakpoint");
		itemRepository.saveAll(oldItems);

	}

	public void run() {

	}
}
