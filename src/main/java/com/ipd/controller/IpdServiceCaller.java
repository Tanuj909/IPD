//package com.ipd.controller;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.ipd.dto.SendServiceRequest;
//
//import java.util.Arrays;
//import java.util.List;
//
//@RestController
//@RequestMapping("api/ipd/service")
//public class IpdServiceCaller {
//
//    private static final String BASE_URL = "http://localhost:3005/api/ipd/billing";
//    private final RestTemplate restTemplate;
//
//    public IpdServiceCaller() {
//        this.restTemplate = new RestTemplate();
//    }
//
//    @PostMapping("/set/{billindId}")
//    public List<SendServiceRequest> addServicesToBilling(@RequestBody SendServiceRequest sendServiceRequest,
//    		@PathVariable Long billingId) {
//    	
//        String url = BASE_URL + "/" + billingId + "/services";
//        SendServiceRequest sendServiceRequest2 = new SendServiceRequest();
//        sendServiceRequest2.setIpdBillingId(billingId);
//        sendServiceRequest2.setServices(sendServiceRequest.getServices());
//        // Set headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<SendServiceRequest> entity = new HttpEntity<>(sendServiceRequest, headers);
//
//        // Make the call
//        ResponseEntity<SendServiceRequest> response = restTemplate.postForEntity(
//                url, entity, SendServiceRequest.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return Arrays.asList(response.getBody());
//        } else {
//            throw new RuntimeException("Failed to add services: " + response.getStatusCode());
//        }
//    }
//}


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

        // The target service expects only { services: [...] }
        // It will set ipdBillingId from path variable itself
        var payload = new SendServiceRequest();
        payload.setServices(request.getServices());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SendServiceRequest> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Object[]> response = restTemplate.postForEntity(
                    url, entity, Object[].class);

            return ResponseEntity.ok(List.of(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error: " + e.getMessage()));
        }
    }
}