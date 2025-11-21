package com.ipd.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipd.entity.IpdDischargeSummary;
import com.ipd.service.IpdDischargeSummaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ipd/discharge-summary")
public class IpdDischargeSummaryController {

    private final IpdDischargeSummaryService service;

    @PostMapping("/create/{admissionId}")
    public ResponseEntity<IpdDischargeSummary> create(
            @PathVariable Long admissionId,
            @RequestBody IpdDischargeSummary request) {
        return ResponseEntity.ok(service.create(admissionId, request));
    }

    @PutMapping("/update/{summaryId}")
    public ResponseEntity<IpdDischargeSummary> update(
            @PathVariable Long summaryId,
            @RequestBody IpdDischargeSummary request) {
        return ResponseEntity.ok(service.update(summaryId, request));
    }

    @DeleteMapping("/delete/{summaryId}")
    public ResponseEntity<String> delete(@PathVariable Long summaryId) {
        service.delete(summaryId);
        return ResponseEntity.ok("Discharge Summary Deleted Successfully");
    }

    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<IpdDischargeSummary> getByAdmission(@PathVariable Long admissionId) {
        return ResponseEntity.ok(service.getByAdmission(admissionId));
    }

    @GetMapping("/hospital")
    public ResponseEntity<List<IpdDischargeSummary>> getAllByHospital() {
        return ResponseEntity.ok(service.getAllByHospital());
    }
}
