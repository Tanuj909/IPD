package com.ipd.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.DTO.PatientDTO;
import com.user.entity.Patient;
import com.user.entity.User;
import com.user.maaper.PatientMapper;
import com.user.repository.UserRepository;
import com.ipd.service.PatientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public List<PatientDTO> getAllPatients(@AuthenticationPrincipal UserDetails userDetails) {
    	
    	List<Patient> patients = patientService.getAllPatients(getCurrentUser(userDetails));
    	List<PatientDTO> dtos = patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
        return dtos;
//        return patientService.getAllPatients(getCurrentUser(userDetails));
    }

    @GetMapping("/{patientId}")
    public PatientDTO getPatientById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long patientId) {
        return patientService.getPatientById(getCurrentUser(userDetails), patientId);
    }

    @GetMapping("/filter/name")
    public List<Patient> filterByName(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String name) {
        return patientService.filterByName(getCurrentUser(userDetails), name);
    }

    @GetMapping("/filter/email")
    public List<Patient> filterByEmail(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String email) {
        return patientService.filterByEmail(getCurrentUser(userDetails), email);
    }
}
