package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    @Value("${billing.base.url}")
    private String billingBaseUrl;

    public void callBillingAPI() {
        String url = billingBaseUrl + "doctor-visit";
        System.out.println("Calling: " + url);
    }
}