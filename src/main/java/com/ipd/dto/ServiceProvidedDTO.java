// ──────────────────────────────────────────────────────────────
//  ServiceProvidedDTO.java
// ──────────────────────────────────────────────────────────────
package com.ipd.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceProvidedDTO {
    private String serviceName;
    private double cost;
    private LocalDateTime providedDate;
}