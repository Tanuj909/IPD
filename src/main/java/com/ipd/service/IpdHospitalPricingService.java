package com.ipd.service;

import java.util.Optional;

import com.ipd.entity.IpdHospitalPricing;

public interface IpdHospitalPricingService{
	
//	IpdHospitalPricing createPricing(IpdHospitalPricing pricing);
	
	IpdHospitalPricing createOrUpdatePricing(IpdHospitalPricing pricing);
	
	Optional<IpdHospitalPricing> getPricingByHospital(Long hospitalId);
}
