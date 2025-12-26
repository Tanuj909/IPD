// SendServiceRequest.java
package com.ipd.dto;

import lombok.Data;
import java.util.List;

@Data
public class SendServiceRequest {
    private Long ipdBillingId;
    private List<ServiceItem> services;

    @Data
    public static class ServiceItem {
        private String serviceName;
        private Double price;
        private Integer quantity = 1;
        private Double gstPercentage = 0.0;  // NEW: Critical for correct GST
        private String serviceType;          // Optional: "DIAGNOSTIC", "NURSING", etc.
        private String isDaily;
    }
}