package com.ipd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
public class IpdBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId;

    @OneToOne
    @JoinColumn(name = "admission_id")
    @JsonIgnore
    private IpdAdmission admission;

    private double roomCharges;
    private double doctorFee;
    private double miscellaneous;
    private double discount;

    private double totalAmount;
    private double finalAmount;

    private boolean paid;

    private String paymentMode; // CASH or ONLINE

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private int doctorVisitCount;

    private LocalDateTime lastVisitDate;

    private LocalDateTime generatedAt;
    private LocalDateTime paidAt;
}
