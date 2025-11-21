package com.ipd.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipd.entity.IpdMedication;
import com.ipd.service.IpdMedicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ipd/medications")
@RequiredArgsConstructor
public class IpdMedicationController {

    private final IpdMedicationService medicationService;

    // 1. CREATE MEDICATION
    @PostMapping("/create")
    public ResponseEntity<IpdMedication> createMedication(
            @RequestParam Long admissionId,
            @RequestParam(required = false) Long treatmentUpdateId,
            @RequestBody IpdMedication medication) {

        return ResponseEntity.ok(
                medicationService.createMedication(medication, admissionId, treatmentUpdateId)
        );
    }

    // 2. GET ALL MEDICATIONS OF ADMISSION
    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<List<IpdMedication>> getByAdmission(@PathVariable Long admissionId) {
        return ResponseEntity.ok(medicationService.getMedicationsByAdmission(admissionId));
    }

    // 3. GET BY TREATMENT UPDATE
    @GetMapping("/update/{treatUpdateId}")
    public ResponseEntity<List<IpdMedication>> getByUpdate(@PathVariable Long treatUpdateId) {
        return ResponseEntity.ok(medicationService.getMedicationsByTreatmentUpdate(treatUpdateId));
    }

    // 4. UPDATE MEDICATION
    @PutMapping("/{medicationId}")
    public ResponseEntity<IpdMedication> updateMedication(
            @PathVariable Long medicationId,
            @RequestBody IpdMedication medication) {

        return ResponseEntity.ok(medicationService.updateMedication(medicationId, medication));
    }

    // 5. DELETE MEDICATION
    @DeleteMapping("/{medicationId}")
    public ResponseEntity<String> deleteMedication(@PathVariable Long medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.ok("Medication Deleted Successfully");
    }
}
