package com.ipd.repository;

import com.ipd.entity.IpdLabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IpdLabTestRepository extends JpaRepository<IpdLabTest, Long> {

	List<IpdLabTest> findByAdmissionId(Long admissionId);
	
}