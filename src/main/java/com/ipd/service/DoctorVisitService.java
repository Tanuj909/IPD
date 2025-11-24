package com.ipd.service;

import java.util.List;

import com.ipd.entity.IpdDoctorVisit;

public interface DoctorVisitService {
	
    IpdDoctorVisit updateVisitCount(Long visitId, Integer visitCount);

    List<IpdDoctorVisit> getVisitsByAdmission(Long admissionId);

    double calculateTotalDoctorFees(Long admissionId);

}
