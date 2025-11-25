package com.ipd.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public class IpdRecommendationCreateDTO {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Reason for recommendation is required")
    private String reason;

    // Getters & Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
