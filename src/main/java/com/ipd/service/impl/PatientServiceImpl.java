package com.ipd.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ipd.service.PatientService;
import com.user.entity.Admin;
import com.user.entity.Patient;
import com.user.entity.Staff;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.AdminRepository;
import com.user.repository.PatientRepository;
import com.user.repository.StaffRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;
    private final PatientRepository patientRepository;

    private Admin resolveAdmin(User creator) {
        if (creator.getRole() == Role.ADMIN) {
            return adminRepository.findByUser(creator)
                    .orElseThrow(() -> new EntityNotFoundException("Admin not found for user " + creator.getId()));
        } else {
            Staff staff = staffRepository.findByUser(creator)
                    .orElseThrow(() -> new EntityNotFoundException("Staff not found for user " + creator.getId()));
            return staff.getAdmin();
        }
    }
    private void checkOwnership(User currentUser, Patient patient) {
        Admin admin = resolveAdmin(currentUser);
        if (!patient.getAdmin().getId().equals(admin.getId())) {
            throw new SecurityException("Unauthorized: You do not own this patient");
        }
    }    

    @Override
    public List<Patient> getAllPatients(User currentUser) {
        Admin admin = resolveAdmin(currentUser);
        return patientRepository.findAllByAdminOrderByUser_NameAsc(admin);
    }

    @Override
    public Patient getPatientById(User currentUser, Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        checkOwnership(currentUser, patient);
        return patient;
    }

    @Override
    public List<Patient> filterByName(User currentUser, String name) {
        Admin admin = resolveAdmin(currentUser);
        return patientRepository.findByAdminAndUser_NameContainingIgnoreCase(admin, name);
    }

    @Override
    public List<Patient> filterByEmail(User currentUser, String email) {
        Admin admin = resolveAdmin(currentUser);
        return patientRepository.findByAdminAndUser_EmailContainingIgnoreCase(admin, email);
    }
}