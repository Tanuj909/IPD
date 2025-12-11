package com.ipd.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class MiscellaneousCharges {
	
	private String itemName;
	private Double charge;

}
