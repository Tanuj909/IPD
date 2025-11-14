// ──────────────────────────────────────────────────────────────
//  DoctorVisitDTO.java
// ──────────────────────────────────────────────────────────────
package com.ipd.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class DoctorVisitDTO {
    private LocalDateTime visitDate;
    private double fee;
}