package com.ipd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ipd.entity.IpdBilling;

@Repository
public interface IpdBillingRepository extends JpaRepository<IpdBilling, Long> {

    Optional<IpdBilling> findByAdmissionId(Long admissionId);

    List<IpdBilling> findByPaidFalse();
    
    @Query("SELECT b FROM IpdBilling b WHERE b.admission.hospital.id = :hospitalId")
    List<IpdBilling> findByHospital_HospitalId(@Param("hospitalId") Long hospitalId);

    
}