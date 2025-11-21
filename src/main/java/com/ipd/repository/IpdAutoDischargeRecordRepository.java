package com.ipd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ipd.entity.IpdAutoDischargeRecord;

public interface IpdAutoDischargeRecordRepository extends JpaRepository<IpdAutoDischargeRecord, Long> {

	Optional<IpdAutoDischargeRecord> findByAdmission_Id(Long admissionId);
	
}
