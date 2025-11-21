package com.ipd.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdDischargeSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String finalDiagnosis;

    @Column(columnDefinition = "TEXT")
    private String summaryNotes;
    
    @Column(columnDefinition = "TEXT")
    private String followUpInstructions;

    @Column(columnDefinition = "TEXT")
    private String dischargeAdvice;

    private LocalDateTime createdAt;
    private Long createdBy;
    
    @OneToOne
    private IpdOutcome outcome;

    @OneToOne
    @JsonIgnore
    private IpdAdmission admission;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;
}
