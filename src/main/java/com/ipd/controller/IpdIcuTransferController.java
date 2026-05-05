package com.ipd.controller;

import com.ipd.dto.transfer.IcuToIpdRequest;
import com.ipd.dto.transfer.IpdToIcuRequest;
import com.ipd.entity.IpdAdmission;
import com.ipd.service.IpdIcuTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ipd/transfers")
@RequiredArgsConstructor
public class IpdIcuTransferController {

    private final IpdIcuTransferService transferService;
    
    @PostMapping("/from-icu")
    public ResponseEntity<IpdAdmission> receiveFromIcu(@RequestBody IcuToIpdRequest request) {
        IpdAdmission admission = transferService.receiveFromIcu(request);
        return ResponseEntity.ok(admission);
    }

    @PostMapping("/to-icu")
    public ResponseEntity<String> transferToIcu(@RequestBody IpdToIcuRequest request) {
        transferService.transferToIcu(request);
        return ResponseEntity.ok("Patient transferred to ICU");
    }
}