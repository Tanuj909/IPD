package com.ipd.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class MiscellaneousCharges {

    private String itemName;
    private Double charge;

    // TRUE  → GST EXEMPT (medical)
    // FALSE → GST APPLICABLE (non-medical)
    // NULL  → old data (handle safely)
    private Boolean medicalItem;

    // 5 or 18 (only if medicalItem = false)
    private Double gstPercentage;
}
