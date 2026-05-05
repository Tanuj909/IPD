package com.ipd.dto.transfer;

import lombok.Data;

@Data
public class IpdToIcuRequest {
	
	private Long admissionId; // source IPD admission
	private String reason; // e.g. “deterioration, needs intensive care”
	private String transferSummary;
	
}