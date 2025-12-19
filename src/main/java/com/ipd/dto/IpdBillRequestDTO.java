package com.ipd.dto;

import java.time.LocalDate;
import com.ipd.enums.PaymentStauts;
import lombok.Data;

@Data
public class IpdBillRequestDTO {
    private Long patientExternalId;
    private Long hospitalExternalId;
    private Long admissionId;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private double roomRatePerDay;
    private double doctorFee;
    private double medicationCharges;
    private double procedureCharges;
    private double nursingCharges;
    private double diagnosticCharges;
    private double foodCharges;
    private Long daysAdmitted;
    private double miscellaneousCharges;
    private PaymentStauts paymentStatus;
    private double discountPercentage; // e.g. 10.0
    private double gstPercentage;
    private Double totalGstAmount;
    private Double advanceAmount; // ← Add
    private String advancePaymentMode; // ← Add (CASH, UPI, CARD, etc.)
    private Double totalPayment;

}
