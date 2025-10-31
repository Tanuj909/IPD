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

//    @Autowired
//    private AppointmentRepository appointmentRepository;

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

    @Override
    public IpdRecommendationResponseDTO createRecommendation(IpdRecommendationCreateDTO dto) {
        String email = getCurrentUsername();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        Doctor doctor;
        if (user.getRole()==Role.DOCTOR) {
			doctor = user.getDoctor();
		}else {
			throw new AccessDeniedException("Only Doctor Role Allow");
		}

//        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
//                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + dto.getAppointmentId()));

        // Validate appointment is completed and assigned to the doctor
//        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
//            throw new IllegalStateException("IPD recommendations can only be made for completed appointments");
//        }
//        if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
//            throw new AccessDeniedException("You can only recommend IPD for appointments assigned to you");
//        }

//        Patient patient = appointment.getPatient();
        
        IpdHospital hospital = hospitalRepository.findById(user.getIpdHospitalId()).orElseThrow(()->new ResourceNotFoundException("Hospital Not Present"));

        IpdRecommendation recommendation = new IpdRecommendation();
//        recommendation.setPatient(patient);
        recommendation.setDoctorId(doctor.getId());
       
//        recommendation.setAppointment(appointment);
        recommendation.setHospital(hospital);
        recommendation.setReason(dto.getReason());
        recommendation.setStatus(IpdRecommendationStatus.PENDING);

        IpdRecommendation saved = ipdRecommendationRepository.save(recommendation);
        return new IpdRecommendationResponseDTO(saved);
    }

    @Override
    public List<IpdRecommendationResponseDTO> getRecommendationsByPatient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
        
        Patient patient;
        
        if(user.getRole()==Role.PATIENT) {
        	patient = user.getPatient();
        }else {
        	throw new AccessDeniedException("Patient role not present");
        }
        return ipdRecommendationRepository.findByPatientId(patient.getId())
                .stream()
                .map(IpdRecommendationResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<IpdRecommendationResponseDTO> getRecommendationsByDoctor(String email) {
    	
//        Doctor doctor = doctorRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: " + email));
    	
    	 User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
         
         Doctor doctor;
         if (user.getRole()==Role.DOCTOR) {
 			doctor = user.getDoctor();
 		}else {
 			throw new AccessDeniedException("Only Doctor Role Allow");
 		}
    	
        return ipdRecommendationRepository.findByDoctorId(doctor.getId())
                .stream()
                .map(IpdRecommendationResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public IpdAdmission convertToAdmission(Long recommendationId, Long roomId) {
        IpdRecommendation recommendation = ipdRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with ID: " + recommendationId));

        if (recommendation.getStatus() != IpdRecommendationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING recommendations can be converted to admissions");
        }

        IpdAdmission admission = ipdService.admitPatient(
                recommendation.getPatientId(),
                recommendation.getDoctorId(),
                roomId,
                recommendation.getReason()
        );

        recommendation.setStatus(IpdRecommendationStatus.ACCEPTED);
        ipdRecommendationRepository.save(recommendation);

        return admission;
    }
    
    @Override
    public List<IpdRecommendationResponseDTO> getPendingRecommendationsByHospital(IpdHospital hospital) {
        return ipdRecommendationRepository.findByHospitalAndStatus(hospital, IpdRecommendationStatus.PENDING)
                .stream()
                .map(IpdRecommendationResponseDTO::new)
                .collect(Collectors.toList());
    }
}