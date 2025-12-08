package com.ipd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class IpdBed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bed number within the room (1..N)
    private Integer bedNumber;

    // occupied flag
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean occupied = false;

    @ManyToOne
    private IpdRoom room;

    // optional: store the admission using this bed previously/currently (if you want history)
    // @ManyToOne
    // private IpdAdmission admission; // avoid circular mapping for now
}
