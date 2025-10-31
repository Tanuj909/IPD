package com.ipd.service;

import java.util.List;
import java.util.Map;

import com.user.DTO.DoctorDTO;
import com.user.entity.Doctor;

public interface DoctorService {

	Doctor getDoctorById(Long id);

	Map<String, Long> getDoctorCountBySpecialization();

	List<Doctor> getRecentDoctors(int count);

	List<Doctor> getAllDoctorsInHospital();

	List<Doctor> getAllDoctors(String name, String specialization);
	
//	List<DoctorReview> getDoctorReviews(Long doctorId);
	
	DoctorDTO getDoctorByEmail(String Email); // For doctor profile

	List<Doctor> getAllDoctors(String name);
}