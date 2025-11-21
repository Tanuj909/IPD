package com.ipd.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ipd.enums.IpdOutcomeType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private IpdOutcomeType outcomeType;

    @Column(columnDefinition = "TEXT")
    private String notes;      // description, reason

    private LocalDateTime outcomeDate;
    private LocalDateTime updatedAt;

    private Long createdBy;    // doctor/nurse who updated the outcome

    @OneToOne
    @JsonIgnore
    private IpdAdmission admission;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;
}
