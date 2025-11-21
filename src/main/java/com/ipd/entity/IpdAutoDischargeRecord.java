package com.ipd.entity;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class IpdAutoDischargeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    private IpdAdmission admission;

    @ManyToOne
    private IpdHospital hospital;

    private boolean autoDischarged;

    @OneToMany
    private List<IpdTreatmentUpdate> treatmentSummary;
    
    @OneToMany
    private List<IpdVital> admissionVitals;

    @OneToOne
    private IpdDischargeSummary dischargeSummary;

    private LocalDateTime dischargedAt;
}
