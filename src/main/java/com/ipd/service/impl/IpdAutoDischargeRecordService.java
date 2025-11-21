package com.ipd.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.dto.DoctorDto;
import com.ipd.dto.IpdAutoDischargeRecordDTO;
import com.ipd.dto.PatientDto;
import com.ipd.entity.IpdAutoDischargeRecord;
import com.ipd.entity.IpdDischargeSummary;
import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.entity.IpdVital;
import com.ipd.repository.IpdAutoDischargeRecordRepository;
import com.user.entity.Doctor;
import com.user.entity.Patient;
import com.user.entity.User;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpdAutoDischargeRecordService {

    private final IpdAutoDischargeRecordRepository autoRepo;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public IpdAutoDischargeRecordDTO getByAdmission(Long admissionId) {

        IpdAutoDischargeRecord record = autoRepo.findByAdmission_Id(admissionId)
                .orElseThrow(() -> new RuntimeException("Auto discharge record not found"));
        
        Patient patient = patientRepository.findById(record.getAdmission().getPatientId()).orElseThrow(()->new ResourceNotFoundException("Patient Not Found"));
        
        Doctor doctor = doctorRepository.findById(record.getAdmission().getDoctorId()).orElseThrow(()->new ResourceNotFoundException("Doctor Not Found"));
        
        User patientUser = patient.getUser();
        
        User doctorUser = doctor.getUser();
        
        PatientDto patientDto = PatientDto.builder()
        		.id(patient.getId())
        		.name(patientUser.getName())
        		.address(patient.getAddress())
        		.age(patient.getAge())
        		.gender(patient.getGender())
        		.phoneNumber(patient.getPhoneNumber())
        		.build();
        
        DoctorDto doctorDto = DoctorDto.builder()
        		.id(doctor.getId())
        		.name(doctorUser.getName())
        		.specialization(doctor.getSpecialization())
        		.department(doctor.getDepartment())
        		.qualification(doctor.getQualification())
        		.experienceYears(doctor.getExperienceYears())
        		.phoneNumber(doctor.getPhoneNumber())
        		.build();

        IpdAutoDischargeRecordDTO dto = new IpdAutoDischargeRecordDTO();

        dto.setId(record.getId());
        dto.setAdmission(record.getAdmission());
        dto.setHospital(record.getHospital());
        dto.setAutoDischarged(record.isAutoDischarged());
        dto.setDischargedAt(record.getDischargedAt());
        dto.setPatient(patientDto);
        dto.setDoctor(doctorDto);

        // Decode Treatment JSON
        try {
            List<IpdTreatmentUpdate> treatments = new ArrayList<>(record.getTreatmentSummary());
            dto.setTreatments(treatments);
        } catch (Exception ex) {
            throw new RuntimeException("Error parsing treatment JSON", ex);
        }

        // Decode Vital JSON
        try {
            List<IpdVital> vitals =  new ArrayList<>(record.getAdmissionVitals());
                    
            dto.setVitals(vitals);
        } catch (Exception ex) {
            throw new RuntimeException("Error parsing vitals JSON", ex);
        }

        // Discharge Summary
        if (record.getDischargeSummary() != null) {
            IpdDischargeSummary ds = record.getDischargeSummary();
            
//            IpdDischargeSummaryDTO dsDto = new IpdDischargeSummaryDTO();
//
//            dsDto.setId(ds.getId());
//            dsDto.setFinalDiagnosis(ds.getFinalDiagnosis());
//            dsDto.setSummaryNotes(ds.getSummaryNotes());
//            dsDto.setDischargeAdvice(ds.getDischargeAdvice());
//            dsDto.setCreatedBy(ds.getCreatedBy());
//            dsDto.setCreatedAt(ds.getCreatedAt() != null ? ds.getCreatedAt().toString() : null);
//            dsDto.setOutcomeId(ds.getOutcome().getId());

            dto.setDischargeSummary(ds);
        }

        return dto;
    }
}
