package org.CornFarmerNZ.model;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ToString
@Builder
@AllArgsConstructor
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamoDbBean
public class Item {

	public Item() {
		prices = new HashMap<>();
	}

	@Getter(onMethod = @__({@DynamoDbPartitionKey}))
	private String id;
	@Getter
	private Map<LocalDate, String> prices;
	@EqualsAndHashCode.Include
	@Getter
	private String name;
	@EqualsAndHashCode.Include
	@Getter
	private String url;
	@Getter
	private String itemImageUrl;

}
