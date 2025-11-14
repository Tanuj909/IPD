package com.ipd.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipd.entity.IpdHospitalPricing;
import com.ipd.repository.IpdHospitalPricingRepository;
import com.ipd.service.IpdHospitalPricingService;

import jakarta.transaction.Transactional;

@Service
public class IpdHospitalPricingServiceImpl implements IpdHospitalPricingService {

    @Autowired
    private IpdHospitalPricingRepository ipdHospitalPricingRepository;

    @Override
    @Transactional
    public IpdHospitalPricing createOrUpdatePricing(IpdHospitalPricing pricing) {
        return ipdHospitalPricingRepository.save(pricing);
    }

    @Override
    public Optional<IpdHospitalPricing> getPricingByHospital(Long hospitalId) {
        return ipdHospitalPricingRepository.findByHospital_Id(hospitalId);
    }
}