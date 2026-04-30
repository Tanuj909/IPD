package com.ipd.service;

import com.ipd.dto.OTToIPDReturnRequest;

public interface ReceivePatientService {

	void handleReturnRequest(OTToIPDReturnRequest request);

	void acceptFromOT(Long admissionId);

}
