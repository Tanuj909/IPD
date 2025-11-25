package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ipd.dto.IpdBillingDetailsResponse;
import com.ipd.service.BillingIntegrationService;

@Service
public class BillingIntegrationServiceImpl implements BillingIntegrationService{
	
	@Autowired
	private RestTemplate restTemplate;
	
    @Value("${billing.base.url}")   // <-- Inject value from application.properties
    private String billingBaseUrl;

	@Override
	public IpdBillingDetailsResponse getBillingDetails(Long admissionId) {
		String url = billingBaseUrl + "details/" + admissionId;
		
        return restTemplate.getForObject(url, IpdBillingDetailsResponse.class);
	}
	
	
}
