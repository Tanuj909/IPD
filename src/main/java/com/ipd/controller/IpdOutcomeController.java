package com.ipd.controller;

import com.ipd.entity.IpdOutcome;
import com.ipd.service.IpdOutcomeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ipd/outcome")
public class IpdOutcomeController {

    private final IpdOutcomeService service;

    public IpdOutcomeController(IpdOutcomeService service) {
        this.service = service;
    }

    @PostMapping("/create/{admissionId}")
    public ResponseEntity<IpdOutcome> create(
            @PathVariable Long admissionId,
            @RequestBody IpdOutcome req) {

        return ResponseEntity.ok(service.createOutcome(admissionId, req));
    }

    @PutMapping("/update/{outcomeId}")
    public ResponseEntity<IpdOutcome> update(
            @PathVariable Long outcomeId,
            @RequestBody IpdOutcome req) {

        return ResponseEntity.ok(service.updateOutcome(outcomeId, req));
    }

    @DeleteMapping("/delete/{outcomeId}")
    public ResponseEntity<String> delete(@PathVariable Long outcomeId) {
        service.deleteOutcome(outcomeId);
        return ResponseEntity.ok("Outcome deleted & admission discharge reset.");
    }

    @GetMapping("/get/{admissionId}")
    public ResponseEntity<IpdOutcome> get(@PathVariable Long admissionId) {
        return ResponseEntity.ok(service.getOutcome(admissionId));
    }
}
