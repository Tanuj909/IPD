package com.ipd.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipd.entity.IpdVital;
import com.ipd.enums.VitalType;
import com.ipd.service.IpdVitalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vitals")
@RequiredArgsConstructor
public class IpdVitalController {

    private final IpdVitalService vitalService;

    // CREATE VITAL
    @PostMapping("/{admissionId}")
    public ResponseEntity<IpdVital> createVital(
            @PathVariable Long admissionId,
            @RequestBody IpdVital vital) {

        return ResponseEntity.ok(vitalService.createVital(admissionId, vital));
    }

    // UPDATE VITAL
    @PutMapping("/{vitalId}")
    public ResponseEntity<IpdVital> updateVital(
            @PathVariable Long vitalId,
            @RequestBody IpdVital vital) {

        return ResponseEntity.ok(vitalService.updateVital(vitalId, vital));
    }

    // DELETE VITAL
    @DeleteMapping("/{vitalId}")
    public ResponseEntity<String> deleteVital(@PathVariable Long vitalId) {
        vitalService.deleteVital(vitalId);
        return ResponseEntity.ok("Vital deleted successfully");
    }

    // GET ALL VITALS FOR ADMISSION
    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<List<IpdVital>> getAdmissionVitals(@PathVariable Long admissionId) {
        return ResponseEntity.ok(vitalService.getAdmissionVitals(admissionId));
    }

    // GET VITALS BY TYPE
    @GetMapping("/{admissionId}/type/{type}")
    public ResponseEntity<List<IpdVital>> getVitalsByType(
            @PathVariable Long admissionId,
            @PathVariable VitalType type) {

        return ResponseEntity.ok(vitalService.getVitalsByType(admissionId, type));
    }
}
