package com.ipd.service;

import java.util.List;

import com.user.entity.Patient;
import com.user.entity.User;

public interface PatientService {

	List<Patient> getAllPatients(User currentUser);

	Patient getPatientById(User currentUser, Long patientId);

	List<Patient> filterByName(User currentUser, String name);

	List<Patient> filterByEmail(User currentUser, String email);
    
}