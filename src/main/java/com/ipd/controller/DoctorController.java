package com.ipd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ipd.service.DoctorService;
import com.user.DTO.DoctorDTO;
import com.user.entity.Doctor;
import com.user.maaper.DoctorMapper;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

	@Autowired
	private DoctorService doctorService;

	@GetMapping
	public ResponseEntity<List<Doctor>> getAll(@RequestParam(required = false) String name) {
		return ResponseEntity.ok(doctorService.getAllDoctors(name));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Doctor> getById(@PathVariable Long id) {
		return ResponseEntity.ok(doctorService.getDoctorById(id));
	}

	@GetMapping("/specialization-count")
	@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
	public ResponseEntity<Map<String, Long>> getDoctorCountBySpecialization() {
		return ResponseEntity.ok(doctorService.getDoctorCountBySpecialization());
	}

	@GetMapping("/hospital")
	public ResponseEntity<List<DoctorDTO>> getAllDoctorsInHospital() {

		List<Doctor> doctors = doctorService.getAllDoctorsInHospital();

		List<DoctorDTO> dtos = doctors.stream().map(DoctorMapper::toDTO).toList();

//        return ResponseEntity.ok(doctorService.getAllDoctorsInHospital().stream()
//            .map(DoctorResponseDTO::new)
//            .collect(Collectors.toList()));
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/recent")
	public ResponseEntity<List<DoctorDTO>> getRecentDoctors(@RequestParam(defaultValue = "5") int count) {

		List<Doctor> doctors = doctorService.getRecentDoctors(count);

		List<DoctorDTO> dtos = doctors.stream().map(DoctorMapper::toDTO).toList();
//        return ResponseEntity.ok(doctorService.getRecentDoctors(count).stream()
//                .map(DoctorResponseDTO::new)
//                .collect(Collectors.toList()));

		return ResponseEntity.ok(dtos);

	}

	@GetMapping("/filter")
	public ResponseEntity<List<DoctorDTO>> getAllApprovedDoctors(@RequestParam(required = false) String name,
			@RequestParam(required = false) String specialization) {

		List<Doctor> doctors = doctorService.getAllDoctors(name, specialization);

		List<DoctorDTO> dtos = doctors.stream().map(DoctorMapper::toDTO).toList();

		return ResponseEntity.ok(dtos);
//        return ResponseEntity.ok(doctorService.getAllDoctors(name, specialization).stream()
//            .map(DoctorResponseDTO::new)
//            .collect(Collectors.toList()));

	}
	
}