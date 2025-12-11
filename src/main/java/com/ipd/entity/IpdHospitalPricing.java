package com.ipd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "ipd_hospital_pricing")
public class IpdHospitalPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "hospital_id", nullable = false, unique = true)
    private IpdHospital hospital;

    // ----- DAILY FIXED FEES -----
    private double nursingFee;
    private double foodFee;
    private double diagnosticFee;
    private double miscellaneousFee;

    // ----- GLOBAL SETTINGS -----
    private double discountPercentage;   // e.g. 10.0
    private double gstPercentage;        // e.g. 18.0

    // ----- DEFAULT MEDICATION MARKUP -----
//    Medication markup is standard in all hospital billing systems:
//    Hospitals purchase medicine at cost
//    They sell with markup 10–30%
//    If you remove it, your billing cannot calculate profit margins.
    
//    private double medicationMarkupPercentage; // e.g. 20.0 → 20% markup on cost

    // ----- DEFAULT SERVICES (X-ray, MRI, etc.) -----
    @ElementCollection
    @CollectionTable(name = "ipd_default_services",
            joinColumns = @JoinColumn(name = "pricing_id"))
    private List<DefaultService> defaultServices;
    
    // ----- Miscellaneous Charges (X-ray, MRI, etc.) -----
    @ElementCollection
    @CollectionTable(name = "ipd_miscellaneous_charges",
            joinColumns = @JoinColumn(name = "pricing_id"))
    private List<MiscellaneousCharges> miscellaneousCharges;
    
    
    private LocalDateTime pricingCreatedAt = LocalDateTime.now();
}