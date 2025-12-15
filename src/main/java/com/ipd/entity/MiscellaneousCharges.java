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
	private boolean medicalItem;      // true → GST EXEMPT
	private Double gstPercentage;     // 5 or 18 (only if medicalItem=false)

}
