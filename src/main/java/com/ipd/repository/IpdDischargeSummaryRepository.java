package com.ipd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipd.entity.IpdDischargeSummary;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdHospital;

public interface IpdDischargeSummaryRepository extends JpaRepository<IpdDischargeSummary, Long> {

    Optional<IpdDischargeSummary> findByAdmission(IpdAdmission admission);

    List<IpdDischargeSummary> findByHospital(IpdHospital hospital);
}
