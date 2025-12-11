package com.ipd.service.impl;

import com.ipd.entity.*;
import com.ipd.repository.*;
import com.ipd.service.IpdTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IpdTrackingServiceImpl implements IpdTrackingService {

    @Autowired private IpdAdmissionRepository admissionRepo;
    @Autowired private DoctorVisitRepository doctorVisitRepo;
    @Autowired private IpdMedicationRepository medicationRepo;
    @Autowired private IpdServiceRepository serviceRepo;

    @Override
    @Transactional
    public IpdDoctorVisit addDoctorVisit(Long admissionId, Long doctorId, Double fee, String notes) {
        IpdAdmission admission = admissionRied(admissionId);
        IpdDoctorVisit visit = IpdDoctorVisit.builder()
                .admission(admission)
                .doctorId(doctorId)
                .visitDate(LocalDateTime.now())
                .fee(fee)
                .notes(notes)
                .visitCount(1)
                .totalDoctorFees(fee * 1)  // NEW LOGIC
                .build();
        return doctorVisitRepo.save(visit);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IpdDoctorVisit> getDoctorVisits(Long admissionId) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        return doctorVisitRepo.findByAdmission(admission);
    }

    
    @Override
    @Transactional
    public IpdMedication addMedication(Long admissionId, String name, Integer qty, Double pricePerUnit) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        IpdMedication med = IpdMedication.builder()
                .admission(admission)
                .medicineName(name)
                .quantity(qty)
                .pricePerUnit(pricePerUnit)
                .administeredDate(LocalDateTime.now())
                .build();
        return medicationRepo.save(med);
    }

    @Override
    @Transactional
    public IpdServiceRendered addService(Long admissionId, String type, String desc, Double charge) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        IpdServiceRendered service = IpdServiceRendered.builder()
                .admission(admission)
                .serviceType(type)
                .description(desc)
                .charge(charge)
                .dateProvided(LocalDateTime.now())
                .build();
        
        return serviceRepo.save(service);
    }

    private IpdAdmission admissionRied(Long id) {
        return admissionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
    }
    
    
    @Override
    @Transactional(readOnly = true)
    public List<IpdMedication> getMedications(Long admissionId) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        return medicationRepo.findByAdmission(admission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpdServiceRendered> getServices(Long admissionId) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        return serviceRepo.findByAdmission(admission);
    }

}