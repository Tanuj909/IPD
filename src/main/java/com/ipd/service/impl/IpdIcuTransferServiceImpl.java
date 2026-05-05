package com.ipd.service.impl;

import com.ipd.dto.transfer.IcuToIpdRequest;
import com.ipd.dto.transfer.IpdToIcuRequest;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdBed;
import com.ipd.entity.IpdRoom;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdBedRepository;
import com.ipd.repository.IpdRoomRepository;
import com.ipd.service.IpdIcuTransferService;
import com.ipd.Exception.ResourceNotFoundException;
import com.user.entity.User;
import com.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpdIcuTransferServiceImpl implements IpdIcuTransferService {

    private final IpdAdmissionRepository admissionRepo;
    private final IpdRoomRepository roomRepo;
    private final IpdBedRepository bedRepo;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${icu.base.url}")   // e.g. http://localhost:3006/api
    private String icuBaseUrl;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // -----------------------------------------
    //  RECEIVE FROM ICU (inbound)
    // -----------------------------------------
    @Override
    @Transactional
    public IpdAdmission receiveFromIcu(IcuToIpdRequest req) {
        // Basic validation
        if (req.getPatientId() == null || req.getHospitalId() == null) {
            throw new IllegalArgumentException("patientId and hospitalId are required");
        }

        // 1. Check patient is not already admitted in IPD
        admissionRepo.findByPatientIdAndIsDischargedFalse(req.getPatientId())
                .ifPresent(a -> { throw new IllegalStateException("Patient is already admitted in IPD"); });

        // 2. Find a free bed (first available room with a free bed)
        List<IpdRoom> rooms = roomRepo.findAvailableRoomsByHospital(req.getHospitalId());
        if (rooms.isEmpty()) {
            throw new ResourceNotFoundException("No available room in hospital " + req.getHospitalId());
        }
        IpdRoom selectedRoom = rooms.get(0);
        IpdBed freeBed = selectedRoom.getBeds().stream()
                .filter(b -> !b.isOccupied())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free bed in room " + selectedRoom.getRoomNumber()));

        // 3. Occupy bed
        freeBed.setOccupied(true);
        selectedRoom.setOccupiedBeds((int) selectedRoom.getBeds().stream().filter(IpdBed::isOccupied).count());
        roomRepo.save(selectedRoom);

        // 4. Create admission
        IpdAdmission admission = new IpdAdmission();
        admission.setPatientId(req.getPatientId());
        admission.setDoctorId(req.getDoctorId());   // may be null
        admission.setBed(freeBed);
        admission.setHospital(selectedRoom.getHospital());
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setDischarged(false);
        admission.setReasonForAdmission(req.getReason() != null ? req.getReason() : "Transferred from ICU");
        admission.setCreatedAt(LocalDateTime.now());
        admission.setCreatedBy(getCurrentUser().getId());
        admission.setStatus("ADMITTED");
        admission.setCurrentLocation("IPD");
        admission.setTransferredTo("ICU");   // source

        IpdAdmission saved = admissionRepo.save(admission);

        // 5. Billing can be generated later via the standard regenerateBill API
        log.info("ICU patient {} transferred to IPD admission {}", req.getPatientId(), saved.getId());

        return saved;
    }

    // -----------------------------------------
    //  TRANSFER TO ICU (outbound)
    // -----------------------------------------
    @Override
    @Transactional
    public void transferToIcu(IpdToIcuRequest req) {
        IpdAdmission admission = admissionRepo.findById(req.getAdmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("IPD admission not found"));

        // Status checks – same as OT transfer
        if (admission.isDischarged() || admission.isOutcomeCreated()) {
            throw new IllegalStateException("Cannot transfer discharged/outcome-created patient");
        }
        if (!"TRANSFER_READY".equals(admission.getStatus())) {
            admission.setStatus("TRANSFER_READY");
            admission.setUpdatedAt(LocalDateTime.now());
        }

        // Free bed
        if (admission.getBed() != null) {
            IpdBed bed = admission.getBed();
            IpdRoom room = bed.getRoom();
            bed.setOccupied(false);
            room.setOccupiedBeds((int) room.getBeds().stream().filter(IpdBed::isOccupied).count());
            roomRepo.save(room);
            admission.setBed(null);
        }

        // Update admission
        admission.setStatus("TRANSFERRED");
        admission.setCurrentLocation("ICU");
        admission.setTransferredTo("ICU");
        admission.setUpdatedAt(LocalDateTime.now());
        admissionRepo.save(admission);

        try {
            String url = icuBaseUrl + "/api/icu/transfers/from-ipd";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("patientId", admission.getPatientId());
            payload.put("hospitalId", admission.getHospital().getId());
            payload.put("sourceReferenceId", admission.getId());
            payload.put("transferSummary", req.getTransferSummary());
            // You can add vitals if needed

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            log.error("Failed to notify ICU: {}", e.getMessage());
            throw new RuntimeException("ICU module unavailable, transfer aborted", e);
        }
    }
}