package com.ipd.billing.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IpdRoomAllocationDTO {
	
    private Long id;

    private String roomNumber;
    private Integer bedNumber;
    private Double bedPricePerDay;
    private Long daysAdmitted;
    private Double totalRoomCharges;

    private LocalDateTime allocationDate;
    private LocalDateTime releaseDate; // null = current

}
