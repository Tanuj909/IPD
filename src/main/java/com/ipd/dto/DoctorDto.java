package com.ipd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DoctorDto {
	
    private Long id;
    
	private String name;
	
    private String specialization;

    private String qualification;

    private Integer experienceYears;

    private Long phoneNumber;

    private String department;

}