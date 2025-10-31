package com.ipd.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdmissionChartPoint {
	private LocalDateTime date;
	private long count;
}
