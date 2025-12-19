package com.ipd.entity;

import java.time.LocalDateTime;

import com.ipd.enums.VitalType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bloodPressure;   // BP
    private String sugar;           // Sugar Level
    private Integer pulse;          // Pulse Rate
    private Double temperature;     // Temp
    private Integer respirationRate;
    private Integer spo2;

    private String notes;

    private Long createdBy;  // nurse / doctor user id
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;     
    
    @Enumerated(EnumType.STRING)
    private VitalType type;

    @ManyToOne
    private IpdAdmission admission;

    @ManyToOne
    private IpdHospital hospital;
}
