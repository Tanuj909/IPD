package com.ipd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.entity.IpdMedication;

public interface DoctorVisitRepository extends JpaRepository<IpdDoctorVisit, Long> {
	
    List<IpdDoctorVisit> findByAdmissionId(Long admissionId);
    
	List<IpdDoctorVisit> findByAdmission(IpdAdmission admission);
	
	
	//Method to count the Total number of doctor Visits!
	Long countByAdmissionId(Long admissionId);

	 

}