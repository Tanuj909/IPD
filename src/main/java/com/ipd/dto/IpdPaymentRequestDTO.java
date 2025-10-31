package com.ipd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdPaymentRequestDTO {
    private Long admissionId;
    private String paymentMode;
}