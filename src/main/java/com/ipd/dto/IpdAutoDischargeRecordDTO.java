package com.ipd.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdDischargeSummary;
import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.entity.IpdVital;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpdAutoDischargeRecordDTO {

	private Long id;

	private PatientDto patient;
	
	private DoctorDto doctor;
	
	private IpdAdmission admission;
	
	private IpdHospital hospital;

	private boolean autoDischarged;
	
	private LocalDateTime dischargedAt;

	private List<IpdTreatmentResponse> treatments;
	
	private List<IpdVital> vitals;

	private IpdDischargeSummary dischargeSummary;

}