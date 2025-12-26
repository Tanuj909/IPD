package com.ipd.billing.dto;

import lombok.Data;

@Data
public class SpecialDiscountResponseDTO {
	
	private Long admissionId;
	private Double specialDiscountPercentage;
	private Double specialDiscountAmount;
	private Double dueAfterSpecialDiscount;
	private String specialDiscountReason;
}
