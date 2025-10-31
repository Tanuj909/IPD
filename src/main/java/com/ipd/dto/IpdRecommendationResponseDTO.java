package com.ipd.dto;

import com.ipd.entity.IpdRecommendation;
import com.ipd.entity.IpdRecommendationStatus;
import com.user.entity.Doctor;
import com.user.entity.Patient;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

public class IpdRecommendationResponseDTO {
	
	@Autowired 
	private DoctorRepository doctorRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
    private Long recommendationId;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String reason;
    private IpdRecommendationStatus status;
    private LocalDateTime createdAt;

    public IpdRecommendationResponseDTO(IpdRecommendation recommendation) {
    	
    	Doctor doctor = doctorRepository.getById(recommendation.getDoctorId());
    	Patient patient = patientRepository.getById(recommendation.getPatientId());
    	
        this.recommendationId = recommendation.getId();
//        this.appointmentId = recommendation.getAppointment().getAppointmentId();
        this.patientName = patient.getUser().getName();
        this.doctorName = doctor.getUser().getName();
        this.reason = recommendation.getReason();
        this.status = recommendation.getStatus();
        this.createdAt = recommendation.getCreatedAt();
    }

    // Getters and Setters
    public Long getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(Long recommendationId) {
        this.recommendationId = recommendationId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public IpdRecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(IpdRecommendationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
