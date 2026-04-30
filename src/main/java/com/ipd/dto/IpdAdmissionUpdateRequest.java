package com.ipd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdAdmissionUpdateRequest {

    private Long doctorId;
    private Long roomId;
    private Long bedId;   // 🔥 ADD THIS
    private String reasonForAdmission;
    private Long createdBy;
}
