// ──────────────────────────────────────────────────────────────
//  IpdBillGenerationRequestDTO.java
// ──────────────────────────────────────────────────────────────
package com.ipd.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class IpdBillGenerationRequestDTO {
    private Long admissionId;
    private Long patientExternalId;
    private Long hospitalExternalId;

    private LocalDate admissionDate;
    private LocalDate dischargeDate;

    private double roomRatePerDay;

    private List<DoctorVisitDTO> doctorVisits;
    private List<ServiceProvidedDTO> services;
    private List<MedicationDispensedDTO> medications;

    private double nursingChargesPerDay;
    private double diagnosticCharges;
    private double foodChargesPerDay;
    private double miscellaneousCharges;

    private double discountPercentage;
    private double gstPercentage;
}