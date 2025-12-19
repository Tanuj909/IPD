package com.ipd.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class DefaultService {
    private String serviceName; // e.g. "X-Ray", "MRI"
    private double charge; // e.g. 800.0

    private Boolean gstApplicable; // true/false
    private Double gstPercentage; // e.g. 18.0
}