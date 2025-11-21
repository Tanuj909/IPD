package com.ipd.controller;

import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.service.IpdTreatmentUpdateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ipd/treatment")
public class IpdTreatmentUpdateController {

    private final IpdTreatmentUpdateService service;

    public IpdTreatmentUpdateController(IpdTreatmentUpdateService service) {
        this.service = service;
    }

    @PostMapping("/create/{admissionId}")
    public ResponseEntity<IpdTreatmentUpdate> create(@PathVariable Long admissionId,@RequestBody IpdTreatmentUpdate req) {
        return ResponseEntity.ok(service.createTreatment(admissionId, req));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<IpdTreatmentUpdate> update(@PathVariable Long id,
                                                     @RequestBody IpdTreatmentUpdate req) {
        return ResponseEntity.ok(service.updateTreatment(id, req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteTreatment(id);
        return ResponseEntity.ok("Treatment Update Deleted Successfully");
    }

    @GetMapping("/all/{admissionId}")
    public ResponseEntity<List<IpdTreatmentUpdate>> getAllByAdmissionId(@PathVariable Long admissionId) {
        return ResponseEntity.ok(service.getAllTreatments(admissionId));
    }

    @GetMapping("/latest/{admissionId}")
    public ResponseEntity<IpdTreatmentUpdate> latestTreatnmentUpdate(@PathVariable Long admissionId) {
        return ResponseEntity.ok(service.getLatestTreatment(admissionId));
    }
}