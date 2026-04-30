package com.ipd.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ipd.dto.OTToIPDReturnRequest;
import com.ipd.entity.IpdAdmission;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.service.ReceivePatientService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceivePatientServiceImpl implements ReceivePatientService {
	
	private final IpdAdmissionRepository ipdAdmissionRepo;
	
    @Value("${billing.base.url}") // <-- Inject value from application.properties
    private String billingBaseUrl;
    
    @Value("${ot.base.ipd.url}") // <-- Inject value from application.properties
    private String oTBaseUrl;
    
    @Autowired
    private RestTemplate restTemplate;
	
//-------------------------------------API to receive Incoming Return Request from OT of Patient----------------------//
	@Override
	@Transactional
	public void handleReturnRequest(OTToIPDReturnRequest request) {

	    /* ================= FETCH ================= */
	    IpdAdmission admission = ipdAdmissionRepo.findById(request.getAdmissionId())
	            .orElseThrow(() -> new RuntimeException("Admission not found"));

	    /* ================= VALIDATIONS ================= */

	    // Patient match
	    if (!admission.getPatientId().equals(request.getPatientId())) {
	        throw new IllegalStateException("Patient mismatch");
	    }

	    // Must be currently in OT
	    if (!"OT".equalsIgnoreCase(admission.getTransferredTo())) {
	        throw new IllegalStateException("Patient is not in OT");
	    }

	    // Already requested (idempotent)
	    if ("RETURN_READY".equalsIgnoreCase(admission.getStatus())) {
	        return;
	    }

	    /* ================= UPDATE STATE ================= */

	    admission.setStatus("RETURN_READY");   // 🔥 key state
	    admission.setUpdatedAt(LocalDateTime.now());

	    ipdAdmissionRepo.save(admission);
	}

	
	@Override
	@Transactional
	public void acceptFromOT(Long admissionId) {

	    IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
	            .orElseThrow(() -> new RuntimeException("Admission not found"));

	    /* ================= VALIDATIONS ================= */

	    if (!"RETURN_READY".equalsIgnoreCase(admission.getStatus())) {
	        throw new IllegalStateException("Patient is not ready to return from OT");
	    }

	    if (!"OT".equalsIgnoreCase(admission.getCurrentLocation())) {
	        throw new IllegalStateException("Patient is not in OT");
	    }

	    /* ================= UPDATE STATE ================= */

	    admission.setStatus("ACTIVE");
	    admission.setCurrentLocation("IPD");
	    admission.setUpdatedAt(LocalDateTime.now());

	    ipdAdmissionRepo.save(admission);

	    /* ================= RESUME BILLING ================= */

	    try {
	        String url = billingBaseUrl + "/ipd/resume-bill/" + admissionId;

	        restTemplate.exchange(
	                url,
	                HttpMethod.PUT,
	                null,
	                Void.class
	        );

	    } catch (Exception e) {
	        log.error("Billing resume failed", e);
	        throw new IllegalStateException("Billing resume failed");
	    }

	    /* ================= CALL OT ================= */

	    try {
	        String url = oTBaseUrl + "/ot/mark-accepted-by-ipd";

	        Map<String, Long> payload = new HashMap<>();
	        payload.put("admissionId", admissionId);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);

	        HttpEntity<Map<String, Long>> entity = new HttpEntity<>(payload, headers);

	        restTemplate.exchange(
	                url,
	                HttpMethod.PUT,
	                entity,
	                Void.class
	        );

	    } catch (Exception e) {
	        log.error("Failed to update OT status", e);
	        // DO NOT THROW
	    }
	}
}
