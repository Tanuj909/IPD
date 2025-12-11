package com.ipd.service.impl;

import com.user.entity.Doctor;
import com.ipd.entity.IpdHospital;
import com.user.entity.Patient;
import com.user.entity.User;
import com.user.enums.Role;
import com.ipd.dto.IpdRecommendationCreateDTO;
import com.ipd.dto.IpdRecommendationResponseDTO;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdRecommendation;
import com.ipd.entity.IpdRecommendationStatus;
import com.user.repository.DoctorRepository;
import com.ipd.repository.IpdHospitalRepository;
import com.user.repository.PatientRepository;
import com.user.repository.UserRepository;
import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.repository.IpdRecommendationRepository;
import com.ipd.service.IpdRecommendationService;
import com.ipd.service.IpdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IpdRecommendationServiceImpl implements IpdRecommendationService {

    @Autowired
    private IpdRecommendationRepository ipdRecommendationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private IpdHospitalRepository hospitalRepository;

    @Autowired
    private IpdService ipdService;


    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    // -------------------------------------------------------------
    // CREATE RECOMMENDATION
    // -------------------------------------------------------------
    @Override
    public IpdRecommendationResponseDTO createRecommendation(IpdRecommendationCreateDTO dto) {

        String email = getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.getRole() != Role.DOCTOR) {
            throw new AccessDeniedException("Only Doctor Role is allowed to create recommendations");
        }

        Doctor doctor = user.getDoctor();

        // --- Fetch patient for this recommendation ---
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + dto.getPatientId()));

        IpdHospital hospital = hospitalRepository.findById(user.getIpdHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital Not Present"));

        // --- Create entity ---
        IpdRecommendation recommendation = new IpdRecommendation();
        recommendation.setDoctorId(doctor.getId());
        recommendation.setPatientId(patient.getId());
        recommendation.setHospital(hospital);
        recommendation.setReason(dto.getReason());
        recommendation.setStatus(IpdRecommendationStatus.PENDING);

        IpdRecommendation saved = ipdRecommendationRepository.save(recommendation);

        return new IpdRecommendationResponseDTO(saved, doctor, patient);
    }


    // -------------------------------------------------------------
    // GET RECOMMENDATIONS BY PATIENT
    // -------------------------------------------------------------
    @Override
    public List<IpdRecommendationResponseDTO> getRecommendationsByPatient(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (user.getRole() != Role.PATIENT) {
            throw new AccessDeniedException("Only patient can view their recommendations");
        }

        Patient patient = user.getPatient();

        return ipdRecommendationRepository.findByPatientId(patient.getId())
                .stream()
                .map(rec -> {
                    Doctor doctor = doctorRepository.findById(rec.getDoctorId()).orElse(null);
                    return new IpdRecommendationResponseDTO(rec, doctor, patient);
                })
                .collect(Collectors.toList());
    }


    // -------------------------------------------------------------
    // GET RECOMMENDATIONS BY DOCTOR
    // -------------------------------------------------------------
    @Override
    public List<IpdRecommendationResponseDTO> getRecommendationsByDoctor(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.DOCTOR) {
            throw new AccessDeniedException("Only doctors can view their recommendations");
        }

        Doctor doctor = user.getDoctor();

        return ipdRecommendationRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(rec -> {

                    Patient patient = patientRepository.findById(rec.getPatientId())
                            .orElse(null);

                    return new IpdRecommendationResponseDTO(rec, doctor, patient);
                })
                .collect(Collectors.toList());
    }


    // -------------------------------------------------------------
    // CONVERT RECOMMENDATION → ADMISSION
    // -------------------------------------------------------------
    @Override
    public IpdAdmission convertToAdmission(Long recommendationId, Long roomId, Long bedId, Double advanceAmount, String advancePaymentMode) {

        IpdRecommendation recommendation = ipdRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));

        if (recommendation.getStatus() != IpdRecommendationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING recommendations can be converted to admission");
        }

        IpdAdmission admission = ipdService.admitPatient(
                recommendation.getPatientId(),
                recommendation.getDoctorId(),
                roomId,
                bedId,
                recommendation.getReason(),
                advanceAmount,       // ← passed from IPD desk
                advancePaymentMode   // ← passed from IPD desk
                
        );

        recommendation.setStatus(IpdRecommendationStatus.ACCEPTED);
        ipdRecommendationRepository.save(recommendation);

        return admission;
    }


    // -------------------------------------------------------------
    // GET PENDING RECOMMENDATIONS BY HOSPITAL
    // -------------------------------------------------------------
    @Override
    public List<IpdRecommendationResponseDTO> getPendingRecommendationsByHospital(IpdHospital hospital) {

        return ipdRecommendationRepository
                .findByHospitalAndStatus(hospital, IpdRecommendationStatus.PENDING)
                .stream()
                .map(rec -> {

                    Doctor doctor = doctorRepository.findById(rec.getDoctorId())
                            .orElse(null);

                    Patient patient = patientRepository.findById(rec.getPatientId())
                            .orElse(null);

                    return new IpdRecommendationResponseDTO(rec, doctor, patient);
                })
                .collect(Collectors.toList());
    }

}
