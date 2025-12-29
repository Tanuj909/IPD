package com.ipd.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ipd.billing.dto.IpdRoomAllocationDTO;

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
    private Double discountPercentage;
    private Double discountAmount;
//    private Double gstPercentage;
    private Double gstAmount;
    private Double totalBeforeDiscount;
    private Double totalAfterDiscountAndGst;
    private List<IPDServiceUsageDTO> ipdServices;  // NEW: Add this field to match the JSON
    private List<IpdRoomAllocationDTO> ipdRooms;
    private Double advanceAmount;
    private Double dueAmmount;
    private String billingStatus;
    private Double totalPayedAmmount;
    private Double dueTotalPayable;
    private Double specialDiscountAmount;   
    public Double getSpecialDiscountAmount() {
		return specialDiscountAmount;
	}
	public void setSpecialDiscountAmount(Double specialDiscountAmount) {
		this.specialDiscountAmount = specialDiscountAmount;
	}
	public Double getSpecialDiscountPercentage() {
		return specialDiscountPercentage;
	}
	public void setSpecialDiscountPercentage(Double specialDiscountPercentage) {
		this.specialDiscountPercentage = specialDiscountPercentage;
	}
	public Double getDueAfterSpecialDiscount() {
		return dueAfterSpecialDiscount;
	}
	public void setDueAfterSpecialDiscount(Double dueAfterSpecialDiscount) {
		this.dueAfterSpecialDiscount = dueAfterSpecialDiscount;
	}
	public String getSpecialDiscountReason() {
		return specialDiscountReason;
	}
	public void setSpecialDiscountReason(String specialDiscountReason) {
		this.specialDiscountReason = specialDiscountReason;
	}
	private Double specialDiscountPercentage;
    private Double dueAfterSpecialDiscount;
    private String specialDiscountReason;          

    

    // Inner DTO for BillingMaster (unchanged)
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
        private String advancePaymentMode;

        // Getters and Setters (unchanged)
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
		public String getAdvancePaymentMode() {
			return advancePaymentMode;
		}
		public void setAdvancePaymentMode(String advancePaymentMode) {
			this.advancePaymentMode = advancePaymentMode;
		}
    }

    // NEW: Nested DTO for each service item (matches the JSON structure)
    public static class IPDServiceUsageDTO {
        private Long id;
        private String serviceName;
        private Double price;
        private Integer quantity;
        private Double totalAmount;
        private LocalDateTime serviceAddDate;
        private Double gstPercentage;
        private Double gstAmount;
        private String isDaily;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        public LocalDateTime getServiceAddDate() { return serviceAddDate; }
        public void setServiceAddDate(LocalDateTime serviceAddDate) { this.serviceAddDate = serviceAddDate; }
		public Double getGstAmount() {
			return gstAmount;
		}
		public void setGstAmount(Double gstAmount) {
			this.gstAmount = gstAmount;
		}
		public Double getGstPercentage() {
			return gstPercentage;
		}
		public void setGstPercentage(Double gstPercentage) {
			this.gstPercentage = gstPercentage;
		}
		public String getIsDaily() {
			return isDaily;
		}
		public void setIsDaily(String isDaily) {
			this.isDaily = isDaily;
		}
    }

    // Getters and Setters for outer class (add the new ones for ipdServices)
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
    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
//    public Double getGstPercentage() { return gstPercentage; }
//    public void setGstPercentage(Double gstPercentage) { this.gstPercentage = gstPercentage; }
    public Double getGstAmount() { return gstAmount; }
    public void setGstAmount(Double gstAmount) { this.gstAmount = gstAmount; }
    public Double getTotalBeforeDiscount() { return totalBeforeDiscount; }
    public void setTotalBeforeDiscount(Double totalBeforeDiscount) { this.totalBeforeDiscount = totalBeforeDiscount; }
    public Double getTotalAfterDiscountAndGst() { return totalAfterDiscountAndGst; }
    public void setTotalAfterDiscountAndGst(Double totalAfterDiscountAndGst) { this.totalAfterDiscountAndGst = totalAfterDiscountAndGst; }
    public List<IPDServiceUsageDTO> getIpdServices() { return ipdServices; }  // NEW
    public void setIpdServices(List<IPDServiceUsageDTO> ipdServices) { this.ipdServices = ipdServices; }  // NEW
	public Double getAdvanceAmount() {
		return advanceAmount;
	}
	public void setAdvanceAmount(Double advanceAmount) {
		this.advanceAmount = advanceAmount;
	}
	public Double getDueAmmount() {
		return dueAmmount;
	}
	public void setDueAmmount(Double dueAmmount) {
		this.dueAmmount = dueAmmount;
	}
	public String getBillingStatus() {
		return billingStatus;
	}
	public void setBillingStatus(String billingStatus) {
		this.billingStatus = billingStatus;
	}
	public Double getTotalPayedAmmount() {
		return totalPayedAmmount;
	}
	public void setTotalPayedAmmount(Double totalPayedAmmount) {
		this.totalPayedAmmount = totalPayedAmmount;
	}
	public Double getDueTotalPayable() {
		return dueTotalPayable;
	}
	public void setDueTotalPayable(Double dueTotalPayable) {
		this.dueTotalPayable = dueTotalPayable;
	}
	public List<IpdRoomAllocationDTO> getIpdRooms() {
		return ipdRooms;
	}
	public void setIpdRooms(List<IpdRoomAllocationDTO> ipdRooms) {
		this.ipdRooms = ipdRooms;
	}
}