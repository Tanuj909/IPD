package com.ipd.service;

import com.ipd.dto.IpdBillingDetailsResponse;

public interface BillingIntegrationService {

	    IpdBillingDetailsResponse getBillingDetails(Long admissionId);

}
