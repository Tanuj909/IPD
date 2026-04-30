package com.ipd.service;

import com.ipd.dto.TransferStatusResponse;
import com.ipd.transfer.dto.OTTransferRequestDTO;

public interface TransferPatientService {

	void makePatientReadyForTransfer(Long admissionId);

	void transferPatientToOT(Long admissionId, OTTransferRequestDTO request);

	TransferStatusResponse getTransferStatus(Long admissionId);

	String getCurrentLocation(Long admissionId);

}
