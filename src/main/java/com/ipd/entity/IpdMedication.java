package com.ipd.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ipd_medications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IpdMedication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    private IpdAdmission admission;

    private String medicineName;
    private Integer quantity;
    private Double pricePerUnit;
    private LocalDateTime administeredDate;
}