package com.ipd.controller;

import com.ipd.entity.IpdLabTest;
import com.ipd.service.IpdLabTestService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ipd/lab")
@RequiredArgsConstructor
public class IpdLabTestController {

    private final IpdLabTestService labService;

    // CREATE LAB REPORT
    @PostMapping("/{admissionId}")
    public ResponseEntity<IpdLabTest> createLabReport(
            @PathVariable Long admissionId,
            @RequestBody IpdLabTest labTest) {

        return ResponseEntity.ok(labService.createLabReport(admissionId, labTest));
    }

    // GET ALL LAB REPORTS OF ADMISSION
    @GetMapping("/all/{admissionId}")
    public ResponseEntity<List<IpdLabTest>> getAllByAdmission(@PathVariable Long admissionId) {
        return ResponseEntity.ok(labService.getAllLabReportsByAdmission(admissionId));
    }

    // GET LAB REPORT BY ID
    @GetMapping("/{labTestId}")
    public ResponseEntity<IpdLabTest> getById(@PathVariable Long labTestId) {
        return ResponseEntity.ok(labService.getLabReportById(labTestId));
    }

    // UPDATE LAB REPORT
    @PutMapping("/{labTestId}")
    public ResponseEntity<IpdLabTest> updateLabReport(
            @PathVariable Long labTestId,
            @RequestBody IpdLabTest updatedData) {

        return ResponseEntity.ok(labService.updateLabReport(labTestId, updatedData));
    }

    // DELETE LAB REPORT
    @DeleteMapping("/{labTestId}")
    public ResponseEntity<String> deleteLabReport(@PathVariable Long labTestId) {
        labService.deleteLabReport(labTestId);
        return ResponseEntity.ok("Lab report deleted successfully");
    }
}
