package com.ipd.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ipd.entity.IpdMedication;

import lombok.Data;

@Data
public class IpdTreatmentResponse {
	
    private Long id;
    private String diagnosis;
    private String treatmentNotes;
    private String proceduresDone;
    private String prescriptionText;
    private Long updatedBy; // doctor/nurse
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<IpdMedicationResponse> medications;
}
