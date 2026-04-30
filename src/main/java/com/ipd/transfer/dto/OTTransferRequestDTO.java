package com.ipd.transfer.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OTTransferRequestDTO {

    private Long patientId;
    private Long admissionId;
    private String patientName;
    private LocalDateTime operationDate; // optional
    private String procedureName;
    private Long hospitalId;
    private String complexity; // LOW / MEDIUM / HIGH
}