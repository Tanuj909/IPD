package com.ipd.dto;

import lombok.Data;

@Data
public class OTToIPDReturnRequest {
    private Long patientId;
    private Long admissionId;
}
