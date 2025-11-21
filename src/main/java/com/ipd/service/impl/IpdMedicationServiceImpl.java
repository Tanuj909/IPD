package com.ipd.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdMedication;
import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.repository.IpdMedicationRepository;
import com.ipd.repository.IpdTreatmentUpdateRepository;
import com.ipd.service.IpdMedicationService;
import com.user.entity.User;
import com.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IpdMedicationServiceImpl implements IpdMedicationService {

    private final IpdMedicationRepository medicationRepo;
    private final IpdAdmissionRepository admissionRepo;
    private final IpdHospitalRepository hospitalRepo;
    private final IpdTreatmentUpdateRepository treatmentRepo;
    
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public IpdMedication createMedication(IpdMedication medication,
                                          Long admissionId,
                                          Long treatmentUpdateId) {

        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission Not Found"));

        IpdHospital hospital = hospitalRepo.findById(getCurrentUser().getIpdHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital Not Found"));

        IpdTreatmentUpdate update = null;

        if (treatmentUpdateId != null) {
            update = treatmentRepo.findById(treatmentUpdateId)
                    .orElseThrow(() -> new RuntimeException("Treatment Update Not Found"));
        }

        medication.setAdmission(admission);
        medication.setHospital(hospital);
        medication.setTreatmentUpdate(update);
        medication.setAdministeredDate(LocalDateTime.now());

        return medicationRepo.save(medication);
    }

    @Override
    public List<IpdMedication> getMedicationsByAdmission(Long admissionId) {
        return medicationRepo.findByAdmissionId(admissionId);
    }

    @Override
    public List<IpdMedication> getMedicationsByTreatmentUpdate(Long treatmentUpdateId) {
        return medicationRepo.findByTreatmentUpdateId(treatmentUpdateId);
    }

    @Override
    public IpdMedication updateMedication(Long medicationId, IpdMedication updated) {

        IpdMedication med = medicationRepo.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication Not Found"));

        med.setMedicineName(updated.getMedicineName());
        med.setDosage(updated.getDosage());
        med.setFrequency(updated.getFrequency());
        med.setDuration(updated.getDuration());
        med.setInstructions(updated.getInstructions());
        med.setQuantity(updated.getQuantity());
        med.setPricePerUnit(updated.getPricePerUnit());

        return medicationRepo.save(med);
    }

    @Override
    public void deleteMedication(Long medicationId) {
        if (!medicationRepo.existsById(medicationId)) {
            throw new RuntimeException("Medication Not Found");
        }
        medicationRepo.deleteById(medicationId);
    }
}