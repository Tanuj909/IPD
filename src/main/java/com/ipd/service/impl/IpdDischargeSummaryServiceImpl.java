package com.ipd.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdDischargeSummary;
import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdOutcome;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdDischargeSummaryRepository;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.service.IpdDischargeSummaryService;
import com.user.entity.User;
import com.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpdDischargeSummaryServiceImpl implements IpdDischargeSummaryService {

    private final IpdAdmissionRepository admissionRepo;
    private final IpdDischargeSummaryRepository summaryRepo;
    private final IpdHospitalRepository hospitalRepo;
    
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // -------------------------------------------
    // ⭐ CREATE DISCHARGE SUMMARY
    // -------------------------------------------
    @Override
    public IpdDischargeSummary create(Long admissionId, IpdDischargeSummary request) {

        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        // Check: Outcome must be created first
        if (admission.getOutcome() == null) {
            throw new RuntimeException("Outcome not created yet. Cannot create Discharge Summary.");
        }

        // Check: If already exists → throw exception
        summaryRepo.findByAdmission(admission)
                .ifPresent(s -> {
                    throw new RuntimeException("Discharge Summary already exists for this Admission");
                });

        IpdOutcome outcome = admission.getOutcome();

        IpdDischargeSummary summary = new IpdDischargeSummary();
        summary.setAdmission(admission);
        summary.setOutcome(outcome);
        summary.setHospital(admission.getHospital());

        summary.setFollowUpInstructions(request.getFollowUpInstructions());
        summary.setFinalDiagnosis(request.getFinalDiagnosis());
        summary.setSummaryNotes(request.getSummaryNotes());
        summary.setDischargeAdvice(request.getDischargeAdvice());
        summary.setCreatedAt(LocalDateTime.now());
        summary.setCreatedBy(request.getCreatedBy());

        return summaryRepo.save(summary);
    }

    // UPDATE SUMMARY
    @Override
    public IpdDischargeSummary update(Long summaryId, IpdDischargeSummary request) {

        IpdDischargeSummary summary = summaryRepo.findById(summaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Discharge Summary not found"));

        summary.setFinalDiagnosis(request.getFinalDiagnosis());
        summary.setSummaryNotes(request.getSummaryNotes());
        summary.setDischargeAdvice(request.getDischargeAdvice());

        return summaryRepo.save(summary);
    }

    // DELETE SUMMARY
    @Override
    public void delete(Long summaryId) {
        IpdDischargeSummary summary = summaryRepo.findById(summaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Discharge Summary not found"));

        summaryRepo.delete(summary);
    }

    // GET BY ADMISSION
    @Override
    public IpdDischargeSummary getByAdmission(Long admissionId) {
        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        return summaryRepo.findByAdmission(admission)
                .orElseThrow(() -> new ResourceNotFoundException("Discharge Summary not found"));
    }

    // ⭐ GET ALL BY HOSPITAL
    @Override
    public List<IpdDischargeSummary> getAllByHospital() {
    	Long hospitalId = getCurrentUser().getIpdHospitalId();
        IpdHospital hospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        return summaryRepo.findByHospital(hospital);
    }
}
