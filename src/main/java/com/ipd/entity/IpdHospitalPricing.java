package com.ipd.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ipd_hospital_pricing")
public class IpdHospitalPricing {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "hospital_id")
	private IpdHospital hospital;
	
	private double nursingFee;
	
	private double medicationFee;
	
	private double foodFee;
	
	private double diagnosticFee;
	
	private double miscellaneousFee;
}
