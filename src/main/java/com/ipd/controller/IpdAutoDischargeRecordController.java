package com.ipd.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipd.dto.IpdAutoDischargeRecordDTO;
import com.ipd.service.impl.IpdAutoDischargeRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ipd/auto-discharge")
@RequiredArgsConstructor
public class IpdAutoDischargeRecordController {

    private final IpdAutoDischargeRecordService autoService;

    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<IpdAutoDischargeRecordDTO> getByAdmission(
            @PathVariable Long admissionId) {

        return ResponseEntity.ok(autoService.getByAdmission(admissionId));
    }
}
