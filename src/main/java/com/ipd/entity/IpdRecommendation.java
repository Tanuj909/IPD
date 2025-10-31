package com.ipd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ipd_recommendations")
public class IpdRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="patient_id")
    private Long patientId;

    @Column(name="doctor_id")
    private Long doctorId;

//    @ManyToOne
//    @JoinColumn(name = "appointment_id", nullable = false)
//    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    @JsonIgnore
    private IpdHospital hospital;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IpdRecommendationStatus status = IpdRecommendationStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
}