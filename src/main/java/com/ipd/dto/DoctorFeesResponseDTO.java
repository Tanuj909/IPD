package com.ipd.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DoctorFeesResponseDTO {
    private Long admissionId;
    private Double totalDoctorFees;
    private Integer totalVisitCount;
    private List<DoctorVisitDetailDTO> visitDetails;
    
    public DoctorFeesResponseDTO() {}
    
    public DoctorFeesResponseDTO(Long admissionId, Double totalDoctorFees, Integer totalVisitCount, List<DoctorVisitDetailDTO> visitDetails) {
        this.admissionId = admissionId;
        this.totalDoctorFees = totalDoctorFees;
        this.totalVisitCount = totalVisitCount;
        this.visitDetails = visitDetails;
    }
}

@Getter
@Setter
class DoctorVisitDetailDTO {
    private Long visitId;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private Long consultationFee;
    private Integer visitCount;
    private Double totalFeeForVisit;
    private String visitDate;
    
    public DoctorVisitDetailDTO() {}
    
    public DoctorVisitDetailDTO(Long visitId, Long doctorId, String doctorName, String specialization, 
                               Long consultationFee, Integer visitCount, Double totalFeeForVisit, String visitDate) {
        this.visitId = visitId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
        this.visitCount = visitCount;
        this.totalFeeForVisit = totalFeeForVisit;
        this.visitDate = visitDate;
    }
}