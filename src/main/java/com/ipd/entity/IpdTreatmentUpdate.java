package com.ipd.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdTreatmentUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatmentNotes;

    @Column(columnDefinition = "TEXT")
    private String proceduresDone;

    @Column(columnDefinition = "TEXT")
    private String prescriptionText;

    private Long updatedBy; // doctor/nurse

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JsonIgnore
    private IpdAdmission admission;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;

    @OneToMany(mappedBy = "treatmentUpdate", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<IpdMedication> medications;
}
