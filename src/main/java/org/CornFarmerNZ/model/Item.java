package org.CornFarmerNZ.model;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsExclude;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@EqualsExclude
	int id;
	@Column(name = "ITEM_PRICE")
	@EqualsExclude
	String price;
	@Column(name = "ITEM_NAME")
	@EqualsAndHashCode.Include
	String name;
	@Column(name = "ITEM_URL")
	@EqualsAndHashCode.Include
	String url;
	@Column(name = "ITEM_IMAGE_URL")
	String itemImageUrl;


}
