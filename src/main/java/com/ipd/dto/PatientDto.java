package com.ipd.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatientDto {

	    private Long id;

	    private String name;

	    private String gender;

	    private int age;

	    private String address;

	    private String phoneNumber;

}
