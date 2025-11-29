package com.ipd.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdVital;
import com.ipd.enums.VitalType;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdVitalRepository;
import com.ipd.service.IpdVitalService;
import com.user.entity.User;
import com.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpdVitalServiceImpl implements IpdVitalService {

    private final IpdVitalRepository vitalRepo;
    private final IpdAdmissionRepository admissionRepo;
    private final UserRepository userRepository;
    
    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public IpdVital createVital(Long admissionId, IpdVital vital) {
    	
    	User currentUser = getCurrentUser();

        IpdAdmission admission = admissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        // Fetch vitals of this admission
        List<IpdVital> allVitals = vitalRepo.findByAdmissionId(admissionId);

        // MAX 3 VITALS
//        if (allVitals.size() >= 3) {
//            throw new RuntimeException("You already have 3 vitals (ADMISSION, CURRENT, DISCHARGE). You can only update.");
//        }

        //TYPE RESTRICTIONS
//        List<IpdVital> sameTypeVitals =
//                vitalRepo.findByAdmissionIdAndType(admissionId, vital.getType());

//        if (!sameTypeVitals.isEmpty()) {
//            throw new RuntimeException(
//                    "Vital type " + vital.getType() + " already exists. You can only update this vital."
//            );
//        }

        // Map admission + hospital
        vital.setAdmission(admission);
        vital.setCreatedBy(currentUser.getId());
        vital.setHospital(admission.getHospital());
        vital.setCreatedAt(LocalDateTime.now());

        return vitalRepo.save(vital);
    }

    @Override
    public IpdVital updateVital(Long vitalId, IpdVital newVital) {

        IpdVital existing = vitalRepo.findById(vitalId)
                .orElseThrow(() -> new RuntimeException("Vital not found"));

        // Restriction: User can update only same type
        if (existing.getType() != newVital.getType()) {
            throw new RuntimeException("You can update only the same VITAL TYPE");
        }

        // Update fields
        existing.setBloodPressure(newVital.getBloodPressure());
        existing.setSugar(newVital.getSugar());
        existing.setPulse(newVital.getPulse());
        existing.setTemperature(newVital.getTemperature());
        existing.setRespirationRate(newVital.getRespirationRate());
        existing.setSpo2(newVital.getSpo2());
        existing.setNotes(newVital.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());

        return vitalRepo.save(existing);
    }

    @Override
    public void deleteVital(Long vitalId) {
        vitalRepo.deleteById(vitalId);
    }

    @Override
    public List<IpdVital> getAdmissionVitals(Long admissionId) {
        return vitalRepo.findByAdmissionId(admissionId);
    }

    @Override
    public List<IpdVital> getVitalsByType(Long admissionId, VitalType type) {
        return vitalRepo.findByAdmissionIdAndType(admissionId, type);
    }
}
