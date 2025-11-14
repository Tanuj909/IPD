package com.ipd.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipd.entity.IpdHospitalPricing;
import com.ipd.service.IpdHospitalPricingService;

@RestController
@RequestMapping("/api/ipd/pricing")
public class IpdHospitalPricingController {

    @Autowired
    private IpdHospitalPricingService pricingService;

    @PostMapping("/create")
    public IpdHospitalPricing create(@RequestBody IpdHospitalPricing pricing) {
        return pricingService.createOrUpdatePricing(pricing);
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<IpdHospitalPricing> getByHospital(@PathVariable Long hospitalId) {
        Optional<IpdHospitalPricing> pricing = pricingService.getPricingByHospital(hospitalId);
        return pricing.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public IpdHospitalPricing update(@PathVariable Long id, @RequestBody IpdHospitalPricing pricing) {
        pricing.setId(id);
        return pricingService.createOrUpdatePricing(pricing);
    }
}