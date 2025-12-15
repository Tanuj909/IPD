package com.ipd.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class OtCharges {
	
	private String Servicename; 
	private Double baseCharges;
	private Double hours;
	private Double extraHours;
	private Double extracharges;
}
