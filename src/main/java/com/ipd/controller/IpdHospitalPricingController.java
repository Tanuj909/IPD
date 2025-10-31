package com.ipd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ipd.entity.IpdHospitalPricing;
import com.ipd.service.IpdHospitalPricingService;

@RestController
@RequestMapping("api/ipd/pricing")
public class IpdHospitalPricingController {
	
	@Autowired
	private IpdHospitalPricingService ipdHospitalPricingService;
	
	@PostMapping("/create")
	public IpdHospitalPricing createPricing(@RequestBody IpdHospitalPricing ipdHospitalPricing) {
		return ipdHospitalPricingService.createPricing(ipdHospitalPricing);
	}
}
