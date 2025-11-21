package com.ipd.repository;

import com.ipd.entity.IpdOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IpdOutcomeRepository extends JpaRepository<IpdOutcome, Long> {

    IpdOutcome findByAdmissionId(Long admissionId);
}