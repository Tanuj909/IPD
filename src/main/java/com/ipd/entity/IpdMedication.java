package com.ipd.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IpdMedication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineName;
    private String dosage;
    private String frequency;   // eg: 2 times a day
    private String duration;    // eg: 5 days
    private String instructions;
    private Integer quantity;
    private Double pricePerUnit;
    	
    private LocalDateTime administeredDate;
    
    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    @JsonIgnore
    private IpdAdmission admission;

    @ManyToOne
    private IpdTreatmentUpdate treatmentUpdate;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;
}
