package com.ipd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdDashboardSummary {
	private long totalAdmittedPatients;
	private long totalRooms;
	private long totalBeds;
	private long occupiedBeds;
	private long availableBeds;
}
