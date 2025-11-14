// ──────────────────────────────────────────────────────────────
//  MedicationDispensedDTO.java
// ──────────────────────────────────────────────────────────────
package com.ipd.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicationDispensedDTO {
    private String medicineName;
    private int quantity;
    private double unitPrice;
    private double totalCost;
    private LocalDateTime dispensedDate;
}