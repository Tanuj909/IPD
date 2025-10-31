package com.ipd.dto;

import java.time.LocalDateTime;

public class IpdBillingDetailsResponse {

    private Long id;
    private BillingMasterDTO billingMaster;
    private Long admissionId;
    private Double roomCharges;
    private Double medicationCharges;
    private Double doctorFees;
    private Double nursingCharges;
    private Double diagnosticCharges;
    private Double procedureCharges;
    private Double foodCharges;
    private Double miscellaneousCharges;
    private Integer daysAdmitted;
    private Double total;

    // Inner DTO for BillingMaster
    public static class BillingMasterDTO {
        private Long id;
        private Long hospitaExternallId;
        private Long patientExternalId;
        private Long admissionId;
        private String moduleType;
        private Double totalAmount;
        private String paymentStatus;
        private String paymentMode;
        private LocalDateTime billingDate;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getHospitaExternallId() { return hospitaExternallId; }
        public void setHospitaExternallId(Long hospitaExternallId) { this.hospitaExternallId = hospitaExternallId; }
        public Long getPatientExternalId() { return patientExternalId; }
        public void setPatientExternalId(Long patientExternalId) { this.patientExternalId = patientExternalId; }
        public Long getAdmissionId() { return admissionId; }
        public void setAdmissionId(Long admissionId) { this.admissionId = admissionId; }
        public String getModuleType() { return moduleType; }
        public void setModuleType(String moduleType) { this.moduleType = moduleType; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
        public String getPaymentMode() { return paymentMode; }
        public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
        public LocalDateTime getBillingDate() { return billingDate; }
        public void setBillingDate(LocalDateTime billingDate) { this.billingDate = billingDate; }
    }

    // Getters and Setters for outer class
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BillingMasterDTO getBillingMaster() { return billingMaster; }
    public void setBillingMaster(BillingMasterDTO billingMaster) { this.billingMaster = billingMaster; }
    public Long getAdmissionId() { return admissionId; }
    public void setAdmissionId(Long admissionId) { this.admissionId = admissionId; }
    public Double getRoomCharges() { return roomCharges; }
    public void setRoomCharges(Double roomCharges) { this.roomCharges = roomCharges; }
    public Double getMedicationCharges() { return medicationCharges; }
    public void setMedicationCharges(Double medicationCharges) { this.medicationCharges = medicationCharges; }
    public Double getDoctorFees() { return doctorFees; }
    public void setDoctorFees(Double doctorFees) { this.doctorFees = doctorFees; }
    public Double getNursingCharges() { return nursingCharges; }
    public void setNursingCharges(Double nursingCharges) { this.nursingCharges = nursingCharges; }
    public Double getDiagnosticCharges() { return diagnosticCharges; }
    public void setDiagnosticCharges(Double diagnosticCharges) { this.diagnosticCharges = diagnosticCharges; }
    public Double getProcedureCharges() { return procedureCharges; }
    public void setProcedureCharges(Double procedureCharges) { this.procedureCharges = procedureCharges; }
    public Double getFoodCharges() { return foodCharges; }
    public void setFoodCharges(Double foodCharges) { this.foodCharges = foodCharges; }
    public Double getMiscellaneousCharges() { return miscellaneousCharges; }
    public void setMiscellaneousCharges(Double miscellaneousCharges) { this.miscellaneousCharges = miscellaneousCharges; }
    public Integer getDaysAdmitted() { return daysAdmitted; }
    public void setDaysAdmitted(Integer daysAdmitted) { this.daysAdmitted = daysAdmitted; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
