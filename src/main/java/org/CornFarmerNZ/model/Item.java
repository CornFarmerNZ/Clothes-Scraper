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
	String name;
	@Column(name = "ITEM_URL")
	String url;
	@Column(name = "ITEM_IMAGE_URL")
	String itemImageUrl;

	@Override
	public boolean equals(Object o) {
		if (o.getClass() == Item.class) {
			return this
					.getName()
					.equals(((Item) o).getName());
		}
		return this.equals(o);
	}


}
