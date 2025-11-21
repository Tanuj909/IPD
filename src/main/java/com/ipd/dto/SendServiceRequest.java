//package com.ipd.dto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//
//@Data
//@Getter
//@Setter
//public class SendServiceRequest {
//    private Long ipdBillingId;
//    private List<ServiceItem> services;
//    
//    @Data
//    public static class ServiceItem {
//        private String serviceName;
//        private Double price;
//        private Integer quantity;
//    }
//}

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
        private Integer quantity;
    }
}
