package com.ipd.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdAdmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;
    private boolean isDischarged;
    
    @Column(name="patient_id")
    private Long patientId;
    
    @Column(name="doctor_id")
    private Long doctorId;

    @ManyToOne
//    @JsonIgnore
    private IpdRoom room;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;

    private String reasonForAdmission;

    @OneToOne(mappedBy = "admission", cascade = CascadeType.ALL)
    @JsonIgnore
    private IpdBilling billing;
    
    private Long createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}