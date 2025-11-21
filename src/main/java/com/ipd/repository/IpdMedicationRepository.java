package com.ipd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdMedication;

public interface IpdMedicationRepository extends JpaRepository<IpdMedication, Long> {
	
	List<IpdMedication> findByAdmissionId(Long admissionId);

	List<IpdMedication> findByAdmission(IpdAdmission admission);

	List<IpdMedication> findByTreatmentUpdateId(Long treatmentUpdateId);

}