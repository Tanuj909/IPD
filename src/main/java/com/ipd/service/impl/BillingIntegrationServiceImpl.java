package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ipd.dto.IpdBillingDetailsResponse;
import com.ipd.service.BillingIntegrationService;

@Service
public class BillingIntegrationServiceImpl implements BillingIntegrationService{
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public IpdBillingDetailsResponse getBillingDetails(Long admissionId) {
        String url = "http://localhost:3005/api/billing/details/" + admissionId;
        return restTemplate.getForObject(url, IpdBillingDetailsResponse.class);
	}
	
	
}
