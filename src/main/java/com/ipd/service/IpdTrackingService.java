package com.ipd.service;

import java.util.List;

import com.ipd.entity.IpdDoctorVisit;
import com.ipd.entity.IpdMedication;
import com.ipd.entity.IpdServiceRendered;

public interface IpdTrackingService {
    IpdDoctorVisit addDoctorVisit(Long admissionId, Long doctorId, Double fee, String notes);
    IpdMedication addMedication(Long admissionId, String name, Integer qty, Double pricePerUnit);
    IpdServiceRendered addService(Long admissionId, String type, String desc, Double charge);
    
    List<IpdDoctorVisit> getDoctorVisits(Long admissionId);
    List<IpdMedication> getMedications(Long admissionId);
    List<IpdServiceRendered> getServices(Long admissionId);

}