package org.CornFarmerNZ.service;

import lombok.extern.log4j.Log4j2;
import org.CornFarmerNZ.model.Item;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Log4j2
public class FileWritingService {


	public boolean writeToCsv(List<Item> items) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("src/main/resources/test.csv"), true));
			for (Item item : items) {
				bw.write(item.getName() + ",");
				bw.write(item.getUrl() + ",");
				bw.write("" + item.getPrices() + "\n");
			}
			bw.close();
		} catch (IOException ioe) {
			log.error(ioe);
		}

		return false;
	}


}
