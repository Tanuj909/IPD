package com.ipd.billing.dto;

import lombok.Data;

@Data
public class UpdateIsDailyRequest {
	private Long admissionId;
	private Long serviceUsageId;
    private String isDaily;
}