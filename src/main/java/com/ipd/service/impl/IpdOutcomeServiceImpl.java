package com.ipd.service.impl;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdOutcome;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdOutcomeRepository;
import com.ipd.service.IpdOutcomeService;
import com.user.entity.User;
import com.user.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class IpdOutcomeServiceImpl implements IpdOutcomeService {

	private final IpdOutcomeRepository outcomeRepo;
	private final IpdAdmissionRepository admissionRepo;
	private final RestTemplate restTemplate;
	private final UserRepository userRepository;

	@Value("${billing.base.url}")
	private String billingBaseUrl;

	public IpdOutcomeServiceImpl(IpdOutcomeRepository outcomeRepo, IpdAdmissionRepository admissionRepo,
			RestTemplate restTemplate, UserRepository userRepository) {
		this.outcomeRepo = outcomeRepo;
		this.admissionRepo = admissionRepo;
		this.restTemplate = restTemplate;
		this.userRepository = userRepository;
	}

	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private User getCurrentUser() {
		return userRepository.findByEmail(getCurrentUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}

	@Transactional
	@Override
	public IpdOutcome createOutcome(Long admissionId, IpdOutcome req) {

		IpdAdmission admission = admissionRepo.findById(admissionId)
				.orElseThrow(() -> new RuntimeException("Admission not found: " + admissionId));

		// If already has outcome, prevent duplicate
		if (outcomeRepo.findByAdmissionId(admissionId) != null) {
			throw new IllegalStateException("Outcome already exists for this admission");
		}

//		// Check Billing Status (Must be PAID)
//		String billingApiUrl = billingBaseUrl + "ipd/status?admissionId=" + admissionId;
//
//		ResponseEntity<String> response = restTemplate.getForEntity(billingApiUrl, String.class);
//
//		if (!"PAID".equalsIgnoreCase(response.getBody())) {
//			throw new IllegalStateException("Cannot discharge! Billing not fully paid.");
//		}

		// Save Outcome
		req.setOutcomeDate(LocalDateTime.now());
		req.setAdmission(admission);
		req.setHospital(admission.getHospital());
		req.setCreatedBy(getCurrentUser().getId());

		IpdOutcome saved = outcomeRepo.save(req);

		// Mark Admission as Discharged
		admission.setDischarged(false);
		admission.setOutcomeCreated(true);
		admission.setDischargeDate(LocalDateTime.now());
		admissionRepo.save(admission);

		return saved;
	}

	@Transactional
	@Override
	public IpdOutcome updateOutcome(Long outcomeId, IpdOutcome req) {

		IpdOutcome existing = outcomeRepo.findById(outcomeId)
				.orElseThrow(() -> new RuntimeException("Outcome not found"));

		existing.setOutcomeType(req.getOutcomeType());
		existing.setNotes(req.getNotes());
		existing.setUpdatedAt(LocalDateTime.now());
		existing.setCreatedBy(req.getCreatedBy());

		return outcomeRepo.save(existing);
	}

	@Transactional
	@Override
	public void deleteOutcome(Long outcomeId) {

		IpdOutcome existing = outcomeRepo.findById(outcomeId)
				.orElseThrow(() -> new RuntimeException("Outcome not found"));

		Long admissionId = existing.getAdmission().getId();

		// Remove
		outcomeRepo.deleteById(outcomeId);

		// Update Admission → Not discharged
		IpdAdmission admission = admissionRepo.findById(admissionId)
				.orElseThrow(() -> new RuntimeException("Admission not found"));

		admission.setDischarged(false);
		admission.setDischargeDate(null);

		admissionRepo.save(admission);
	}

	@Override
	public IpdOutcome getOutcome(Long admissionId) {
		return outcomeRepo.findByAdmissionId(admissionId);
	}

}