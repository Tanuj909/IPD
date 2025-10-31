package com.ipd.controller;

import com.ipd.entity.IpdHospital;
import com.ipd.service.IpdHospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class IpdHospitalController {

    @Autowired
    private IpdHospitalService hospitalService;

    @PostMapping
    public ResponseEntity<IpdHospital> createHospital(
            @RequestPart("hospital") IpdHospital hospital,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(hospitalService.createHospital(hospital, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IpdHospital> updateHospital(
            @PathVariable Long id,
            @RequestPart("hospital") IpdHospital hospital,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        return ResponseEntity.ok(hospitalService.updateHospital(id, hospital, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<IpdHospital>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IpdHospital> getHospitalById(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }

    @PostMapping("/{hospitalId}/map-to-user/{userId}")
    public ResponseEntity<IpdHospital> mapHospitalToUser(@PathVariable Long userId, @PathVariable Long hospitalId) {
        return ResponseEntity.ok(hospitalService.mapHospitalToUser(userId, hospitalId));
    }
}
