package com.ipd.billing.dto;

import lombok.Data;

@Data
public class SpecialDiscountRequestDTO {

	private Long admissionId;
	private Double specialDiscountPercentage;
	private String reason;
}
