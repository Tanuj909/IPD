package com.ipd.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.repository.DoctorVisitRepository;
import com.ipd.service.DoctorVisitService;
import com.user.entity.Doctor;
import com.user.repository.DoctorRepository;

@Service
public class DoctorVisitServiceImpl implements DoctorVisitService {

    @Autowired
    private DoctorVisitRepository doctorVisitRepo;
    
    @Autowired
    private DoctorRepository doctorRepo;
     
    @Override
    public IpdDoctorVisit updateVisitCount(Long visitId, Integer visitCount) {
        if (visitCount == null || visitCount < 1) {
            throw new IllegalArgumentException("Visit count must be at least 1");
        }

        IpdDoctorVisit visit = doctorVisitRepo.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor visit not found"));
        
        Doctor doctor = doctorRepo.findById(visit.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        double perVisitFee = (visit.getFee() != null) ? visit.getFee() : doctor.getConsultationFee();

        visit.setVisitCount(visitCount);
        visit.setTotalDoctorFees(perVisitFee * visitCount);   // NEW LOGIC
        
        return doctorVisitRepo.save(visit);
    }

    @Override
    public List<IpdDoctorVisit> getVisitsByAdmission(Long admissionId) {
        return doctorVisitRepo.findByAdmissionId(admissionId);
    }

//    @Override
//    public double calculateTotalDoctorFees(Long admissionId) {
//        List<IpdDoctorVisit> visits = doctorVisitRepo.findByAdmissionId(admissionId);
//
//        return visits.stream()
//                .mapToDouble(visit -> {
//                    Doctor doctor = doctorRepo.findById(visit.getDoctorId())
//                            .orElseThrow(() ->
//                                    new ResourceNotFoundException("Doctor not found with ID: " + visit.getDoctorId()));
//
//                    double perVisitFee = (visit.getFee() != null) ? visit.getFee() : doctor.getConsultationFee(); // Use custom fee if set, else default
//                    return visit.getVisitCount() * perVisitFee;
//                })
//                .sum();
//    }
    
    @Override
    public double calculateTotalDoctorFees(Long admissionId) {
        return doctorVisitRepo.findByAdmissionId(admissionId)
                .stream()
                .mapToDouble(v -> v.getTotalDoctorFees() != null ? v.getTotalDoctorFees() : 0.0)
                .sum();
    }

}