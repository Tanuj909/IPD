package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipd.entity.IpdHospitalPricing;
import com.ipd.repository.IpdHospitalPricingRepository;
import com.ipd.service.IpdHospitalPricingService;

@Service
public class IpdHospitalPricingServiceImpl implements IpdHospitalPricingService {
	
	@Autowired
	private IpdHospitalPricingRepository ipdHospitalPricingRepository;

	@Override
	public IpdHospitalPricing createPricing(IpdHospitalPricing pricing) {
		IpdHospitalPricing roomPrice = ipdHospitalPricingRepository.save(pricing);
		return roomPrice;
	}

}
