package com.ipd.service.impl;

import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.repository.IpdTreatmentUpdateRepository;
import com.ipd.service.IpdTreatmentUpdateService;
import com.user.entity.User;
import com.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IpdTreatmentUpdateServiceImpl implements IpdTreatmentUpdateService {

    private final IpdTreatmentUpdateRepository repo;
    
    @Autowired
    private IpdAdmissionRepository admissionRepository;
    
    @Autowired
    private IpdHospitalRepository hospitalRepository;
    
    @Autowired
    private UserRepository userRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public IpdTreatmentUpdateServiceImpl(IpdTreatmentUpdateRepository repo) {
        this.repo = repo;
    }

    @Override
    public IpdTreatmentUpdate createTreatment(Long AdmissonId, IpdTreatmentUpdate req) {
    	
    	Long hospitalId = getCurrentUser().getIpdHospitalId();
    	
    	IpdHospital hospital = hospitalRepository.findById(hospitalId).orElseThrow(()->new AccessDeniedException("Not Allow IPD module access"));
    	
        req.setCreatedAt(LocalDateTime.now());
        req.setUpdatedAt(LocalDateTime.now());
        IpdAdmission admission = admissionRepository.findById(AdmissonId).orElseThrow(()->new ResourceNotFoundException("Admission Not present"));
        req.setAdmission(admission);
        req.setHospital(hospital);
        return repo.save(req);
    }

    @Override
    public IpdTreatmentUpdate updateTreatment(Long id, IpdTreatmentUpdate req) {
        IpdTreatmentUpdate existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Treatment Record Not Found"));

        existing.setDiagnosis(req.getDiagnosis());
        existing.setTreatmentNotes(req.getTreatmentNotes());
        existing.setProceduresDone(req.getProceduresDone());
        existing.setPrescriptionText(req.getPrescriptionText());
        existing.setUpdatedBy(getCurrentUser().getId());
        existing.setUpdatedAt(LocalDateTime.now());

        return repo.save(existing);
    }

    @Override
    public void deleteTreatment(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<IpdTreatmentUpdate> getAllTreatments(Long admissionId) {
        return repo.findByAdmissionIdOrderByCreatedAtDesc(admissionId);
    }

    @Override
    public IpdTreatmentUpdate getLatestTreatment(Long admissionId) {
        return repo.findFirstByAdmissionIdOrderByCreatedAtDesc(admissionId);
    }
}
