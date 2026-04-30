package com.ipd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ipd.dto.OTToIPDReturnRequest;
import com.ipd.service.ReceivePatientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ipd")
@RequiredArgsConstructor
public class ReceivePatientController {
	
	private final ReceivePatientService receivePatientService;
	
	@PostMapping("/return-request")
	public ResponseEntity<String> receiveReturnRequest(@RequestBody OTToIPDReturnRequest request) {

		receivePatientService.handleReturnRequest(request);

	    return ResponseEntity.ok("Return request received");
	}
	
	@PutMapping("/{admissionId}/accept-from-ot")
	public ResponseEntity<String> acceptFromOT(@PathVariable Long admissionId) {

		receivePatientService.acceptFromOT(admissionId);

	    return ResponseEntity.ok("Patient accepted from OT");
	}

}
