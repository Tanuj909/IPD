// src/main/java/com/ipd/dto/IpdPaymentHistoryResponseDTO.java
package com.ipd.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IpdPaymentHistoryResponseDTO {
    private Long id;
    private Double amount;
    private String paymentMode;
    private LocalDateTime paymentDate;
    private String receiptNo;
    private String paidBy;
}