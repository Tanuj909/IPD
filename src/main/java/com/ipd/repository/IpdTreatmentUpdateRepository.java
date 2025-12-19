package com.ipd.repository;

import com.ipd.entity.IpdTreatmentUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IpdTreatmentUpdateRepository extends JpaRepository<IpdTreatmentUpdate, Long> {

    List<IpdTreatmentUpdate> findByAdmissionIdOrderByCreatedAtDesc(Long admissionId);

    IpdTreatmentUpdate findFirstByAdmissionIdOrderByCreatedAtDesc(Long admissionId);

	boolean existsByAdmissionIdAndCreatedAtBetween(Long id, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
