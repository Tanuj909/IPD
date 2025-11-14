package com.ipd.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ipd_doctor_visits")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IpdDoctorVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    private IpdAdmission admission;

    private Long doctorId;
    private LocalDateTime visitDate;
    private Double fee;
    private String notes;
}