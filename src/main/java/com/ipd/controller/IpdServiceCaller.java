// IpdServiceCaller.java
package com.ipd.controller;

import com.ipd.dto.SendServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/ipd/service")  // Fixed: was "api/ipd/service"
public class IpdServiceCaller {

    private static final String BILLING_SERVICE_URL = "http://localhost:3005/api/ipd/billing";
//    private static final String BILLING_SERVICE_URL = "http://147.93.28.8:3005/api/ipd/billing";


    private final RestTemplate restTemplate;

    @Autowired
    public IpdServiceCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/set/{billingId}")
    public ResponseEntity<List<?>> addServicesToBilling(
            @PathVariable("billingId") Long billingId,
            @RequestBody SendServiceRequest request) {

        String url = BILLING_SERVICE_URL + "/" + billingId + "/services";

        // Preserve all fields including gstPercentage
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SendServiceRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Object[]> response = restTemplate.postForEntity(url, entity, Object[].class);
            return ResponseEntity.ok(List.of(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error adding services: " + e.getMessage()));
        }
    }
}