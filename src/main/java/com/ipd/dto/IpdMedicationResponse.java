package com.ipd.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IpdMedicationResponse {
	
	private Long id;
    private String medicineName;
    private String dosage;
    private String frequency;   // eg: 2 times a day
    private String duration;    // eg: 5 days
    private String instructions;
    private Integer quantity;
    private LocalDateTime administeredDate;
}
