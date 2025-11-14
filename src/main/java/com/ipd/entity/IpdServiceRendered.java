package com.ipd.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ipd_services_rendered")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IpdServiceRendered {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "admission_id", nullable = false)
    private IpdAdmission admission;

    private String serviceType; // e.g., NURSING, DIAGNOSTIC, PROCEDURE, FOOD, MISC
    private String description;
    private Double charge;
    private LocalDateTime dateProvided;
}