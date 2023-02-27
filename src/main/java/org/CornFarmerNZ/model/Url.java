package org.CornFarmerNZ.model;

import lombok.*;
import org.CornFarmerNZ.constants.Store;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@EqualsAndHashCode
@DynamoDbBean
public class Url {

	@Getter(onMethod = @__({@DynamoDbPartitionKey}))
	private String id;
	@Getter
	private String url;
	@Getter
	private Store store;

}
