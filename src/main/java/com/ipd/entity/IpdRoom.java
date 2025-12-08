//package com.ipd.entity;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Entity
//public class IpdRoom {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true)
//    private String roomNumber;
//    private Double price;
//    private String wardType; 
//    private int totalBeds;
//    private int occupiedBeds;
//
//    @ManyToOne
//    @JsonIgnore
//    private IpdHospital hospital;
//
//    private boolean isActive = true;
//}

package com.ipd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class IpdRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roomNumber;
    private Double price;
    private String wardType; 
    private int totalBeds;
    private int occupiedBeds;

    @ManyToOne
    @JsonIgnore
    private IpdHospital hospital;

    private boolean isActive = true;

    //room -> beds
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<IpdBed> beds = new ArrayList<>();
}

