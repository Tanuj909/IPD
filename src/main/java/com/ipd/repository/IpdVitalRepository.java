package com.ipd.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdVital;
import com.ipd.enums.VitalType;

public interface IpdVitalRepository extends JpaRepository<IpdVital, Long> {

    List<IpdVital> findByAdmissionId(Long admissionId);

    List<IpdVital> findByAdmissionIdAndType(Long admissionId, VitalType type);

	boolean existsByAdmissionIdAndCreatedAtBetween(Long id, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
