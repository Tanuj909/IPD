package com.ipd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.entity.IpdMedication;
import com.ipd.entity.IpdServiceRendered;

public interface IpdServiceRepository extends JpaRepository<IpdServiceRendered, Long> {
    List<IpdServiceRendered> findByAdmissionId(Long admissionId);
   
	 
	 List<IpdServiceRendered> findByAdmission(IpdAdmission admission);
}