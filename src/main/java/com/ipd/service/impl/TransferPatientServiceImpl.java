package com.ipd.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ipd.Exception.PatientAlreadyTransferredException;
import com.ipd.dto.TransferStatusResponse;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdBed;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.entity.IpdRoom;
import com.ipd.enums.IsDaily;
import com.ipd.repository.DoctorVisitRepository;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdRoomRepository;
import com.ipd.security.CustomUserDetails;
import com.ipd.service.PatientService;
import com.ipd.service.TransferPatientService;
import com.ipd.transfer.dto.OTTransferRequestDTO;
import com.user.DTO.PatientDTO;
import com.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferPatientServiceImpl implements TransferPatientService{
	
    private final IpdAdmissionRepository ipdAdmissionRepo;
    private final IpdRoomRepository ipdRoomRepo;
    private final DoctorVisitRepository doctorVisitRepo;
    private final PatientService patientService;

    @Value("${billing.base.url}") // <-- Inject value from application.properties
    private String billingBaseUrl;
    
    @Value("${ot.base.ipd.url}") 
    private String otBaseUrl;
    
    @Autowired
    private RestTemplate restTemplate;
    
//    Getting Current Login User
    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No user is logged in");
        }
        return ((CustomUserDetails) auth.getPrincipal()).getUser();
    }
	
//-------------------------------------------Get Patient Transfer Status----------------------------------------------//
    @Override
    public TransferStatusResponse getTransferStatus(Long admissionId) {

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        return TransferStatusResponse.builder()
                .status(admission.getStatus())
                .currentLocation(admission.getCurrentLocation())
                .build();
    }
    
    
//-------------------------------------------Get Patient Transferred Destination----------------------------------------------//
    @Override
    public String getTransferredLocation(Long admissionId) {

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        return admission.getTransferredTo();
    }
    
//-------------------------------------------Making Patient Ready For Transfer ----------------------------------------------//
    @Override
    @Transactional
    public void makePatientReadyForTransfer(Long admissionId) {

        /* ================= STEP 1: CHECK ADMISSION ================= */
        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        if (admission.isDischarged()) {
            throw new IllegalStateException("Patient already discharged");
        }

        /* ================= STEP 2: FREE BED ================= */
        IpdBed bed = admission.getBed();

        if (bed != null) {


        bed.setOccupied(false);

        IpdRoom room = bed.getRoom();
        room.setOccupiedBeds(
                (int) room.getBeds().stream().filter(IpdBed::isOccupied).count()
        );

        ipdRoomRepo.save(room);

        // Remove bed from admission
        admission.setBed(null);
        }

        /* ================= STEP 3: DOCTOR VISITS ================= */
        List<IpdDoctorVisit> visits = doctorVisitRepo.findByAdmissionId(admissionId);

        for (IpdDoctorVisit visit : visits) {
            if (visit.getIsDaily() == IsDaily.YES) {
                visit.setIsDaily(IsDaily.NO);
            }
        }

        doctorVisitRepo.saveAll(visits);

        /* ================= STEP 4: CALL BILLING ================= */

        try {
            // 1️⃣ Pause Bill
            String pauseUrl = billingBaseUrl + "ipd/pause-bill/" + admissionId;

            restTemplate.exchange(
                    pauseUrl,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );

            // 2️⃣ Release Room
            String releaseUrl = billingBaseUrl + "ipd/" + admissionId + "/release-room";

            restTemplate.exchange(
                    releaseUrl,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );

        } catch (Exception e) {
            throw new RuntimeException("Billing API failed: " + e.getMessage());
        }

        /* ================= STEP 5: UPDATE STATUS ================= */
        admission.setStatus("TRANSFER_READY");
        admission.setUpdatedAt(LocalDateTime.now());

        ipdAdmissionRepo.save(admission);
    }
   
    
//-------------------------------------------Cancel Patient Transfer----------------------------------------------//
  
    @Override
    @Transactional
    public void cancelTransfer(Long admissionId) {

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        String status = admission.getStatus();

        // 🔥 Only allow when TRANSFER_READY
        if (!"TRANSFER_READY".equals(status)) {
            throw new IllegalStateException(
                "Cancel transfer allowed only when patient is TRANSFER_READY. Current status: " + status
            );
        }

        // ✅ Revert to ADMITTED
        admission.setStatus("ADMITTED");
        admission.setUpdatedAt(LocalDateTime.now());
        ipdAdmissionRepo.save(admission);

        // ✅ Resume billing
        try {
            String url = billingBaseUrl + "ipd/resume-bill/" + admissionId;

            restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);

        } catch (Exception e) {
            log.error("Billing resume failed", e);
            throw new IllegalStateException("Billing resume failed");
        }
    }
    
    
    
    
//-------------------------------------------Transfer Patient to OT----------------------------------------------//
    @Override
    @Transactional
    public void transferPatientToOT(Long admissionId, OTTransferRequestDTO request) {

        /* ================= STEP 1: FETCH ADMISSION ================= */
        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        if (admission.isDischarged()) {
            throw new IllegalStateException("Patient already discharged");
        }

        if (!"TRANSFER_READY".equalsIgnoreCase(admission.getStatus())) {
            throw new IllegalStateException("Patient is not ready for transfer");
        }
        
        if ("TRANSFERRED".equalsIgnoreCase(admission.getStatus())) {
            throw new IllegalStateException("Patient Already Transfered to:-" + admission.getTransferredTo());
        }

        /* ================= STEP 2: VALIDATE REQUEST ================= */
        if (request.getProcedureName() == null || request.getProcedureName().isBlank()) {
            throw new IllegalArgumentException("Procedure name is required");
        }

        
        // 🔥 COMPLEXITY VALIDATION (ADD HERE)
        List<String> allowed = List.of("MINOR", "MODERATE", "COMPLEX","HIGHLY_COMPLEX");
        
        if (request.getComplexity() == null ||
        	    !allowed.contains(request.getComplexity().toUpperCase())) {

        	    throw new IllegalArgumentException("Invalid complexity. Allowed: LOW, MEDIUM, HIGH");
        	}

        /* ================= STEP 3: BUILD OT PAYLOAD ================= */
        OTTransferRequestDTO dto = new OTTransferRequestDTO();

        dto.setAdmissionId(admissionId);
        dto.setPatientId(admission.getPatientId());
        dto.setPatientName(fetchPatientName(admission.getPatientId())); // 👈 important
        dto.setOperationDate(request.getOperationDate()); // optional
        dto.setProcedureName(request.getProcedureName());
        dto.setHospitalId(admission.getHospital().getId());
        dto.setComplexity(request.getComplexity());

        /* ================= STEP 4: CALL OT ================= */
        try {
//          String url = "http://localhost:3006/api/ot/accept-transfer";
        	String url = otBaseUrl + "/ipd/ot-request";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<OTTransferRequestDTO> entity = new HttpEntity<>(dto, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("OT rejected transfer");
            }

        } catch (Exception e) {
            throw new RuntimeException("OT transfer failed: " + e.getMessage());
        }

        /* ================= STEP 5: UPDATE IPD ================= */
        admission.setStatus("TRANSFERRED");
        admission.setTransferredTo("OT");
        admission.setCurrentLocation("OT");
        admission.setUpdatedAt(LocalDateTime.now());

        ipdAdmissionRepo.save(admission);
    }
    
//------------------------------------------Helper Method to Get Patient Details------------------------------------------//
    private String fetchPatientName(Long patientId) {
    	
    	User user = currentUser();
    	PatientDTO patient = patientService.getPatientById(user, patientId);
    	return patient.getName();
    }
	
}
