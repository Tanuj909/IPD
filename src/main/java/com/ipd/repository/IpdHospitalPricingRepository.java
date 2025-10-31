package com.ipd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdHospitalPricing;

public interface IpdHospitalPricingRepository extends JpaRepository<IpdHospitalPricing, Long>{

	 Optional<IpdHospitalPricing> findByHospital_Id(Long hospitalId);
}
