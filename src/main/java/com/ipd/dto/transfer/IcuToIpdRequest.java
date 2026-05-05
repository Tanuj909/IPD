package com.ipd.dto.transfer;

import lombok.Data;

@Data
public class IcuToIpdRequest {
	
    private Long patientId;
    private String patientName;          // (optional) for display
    private Long hospitalId;
    private Long icuAdmissionId;         // source ICU admission
    private String reason;               // transfer reason
    private String transferSummary;      // clinical notes from ICU
    private Long doctorId;               // nullable – can be assigned later
    private Double advanceAmount;        // optional
    private String advancePaymentMode;   // "CASH"/"ONLINE"
    
}