package com.ipd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipd.dto.TransferStatusResponse;
import com.ipd.service.TransferPatientService;
import com.ipd.transfer.dto.OTTransferRequestDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/ipd")
@RequiredArgsConstructor
public class TransferPatientController {
	
	private final TransferPatientService transferPatientService;
	
	
//-------------------------------------------Get Patient Transfer Status----------------------------------------------//
	@GetMapping("/{admissionId}/transfer-status")
	public ResponseEntity<TransferStatusResponse> getTransferStatus(@PathVariable Long admissionId) {

		TransferStatusResponse transferStatusResponse = transferPatientService.getTransferStatus(admissionId);

	    return ResponseEntity.ok(transferStatusResponse);
	}
	
	
//-------------------------------------------Get Patient Transferred Destination----------------------------------------------//
	@GetMapping("/{admissionId}/location")
	public ResponseEntity<String> getCurrentLocation(@PathVariable Long admissionId) {

	    String location = transferPatientService.getCurrentLocation(admissionId);

	    return ResponseEntity.ok(location);
	}
	
//-------------------------------------------Making Patient Ready For Transfer ----------------------------------------------//
	@PutMapping("/{admissionId}/ready-for-transfer")
	public ResponseEntity<String> makeReady(@PathVariable Long admissionId) {

		transferPatientService.makePatientReadyForTransfer(admissionId);

	    return ResponseEntity.ok("Patient is ready for transfer");
	}
	
//-------------------------------------------Transfer Patient to OT----------------------------------------------//
	@PutMapping("/{admissionId}/transfer-to-ot")
	public ResponseEntity<String> transferToOT(
	        @PathVariable Long admissionId,
	        @RequestBody OTTransferRequestDTO request) {

		transferPatientService.transferPatientToOT(admissionId, request);

	    return ResponseEntity.ok("Patient transferred");
	}
}
