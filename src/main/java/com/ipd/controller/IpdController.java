package com.ipd.controller;

import com.ipd.entity.IpdHospital;
import com.ipd.entity.IpdMedication;
import com.user.entity.Doctor;
import com.user.entity.Patient;
import com.user.entity.User;
import com.ipd.dto.AdmissionChartPoint;
import com.ipd.dto.DoctorVisitDTO;
import com.ipd.dto.IpdBillingDetailsResponse;
import com.ipd.dto.IpdDashboardSummary;
import com.ipd.dto.IpdPaymentRequestDTO;
import com.ipd.dto.IpdRecommendationCreateDTO;
import com.ipd.dto.IpdRecommendationResponseDTO;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdBilling;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.entity.IpdModuleSetting;
import com.ipd.entity.IpdRoom;
import com.ipd.entity.IpdServiceRendered;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;
import com.user.repository.UserRepository;
import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.repository.DoctorVisitRepository;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdBillingRepository;
import com.ipd.repository.IpdHospitalRepository;
import com.ipd.repository.IpdMedicationRepository;
import com.ipd.repository.IpdModuleSettingRepository;
import com.ipd.repository.IpdServiceRepository;
import com.ipd.service.BillingIntegrationService;
import com.ipd.service.IpdRecommendationService;
import com.ipd.service.IpdService;
import com.ipd.service.IpdTrackingService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ipd")
public class IpdController {

    @Autowired
    private IpdService ipdService;
    
    @Autowired
    private IpdModuleSettingRepository ipdModuleSettingRepo;
    
    @Autowired
    private IpdBillingRepository billingRepo;
    
    @Autowired
    private IpdRecommendationService ipdRecommendationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private IpdHospitalRepository hospitalRepository;
    
    @Autowired
    private BillingIntegrationService billingIntegrationService;
    
    @Autowired
    private DoctorVisitRepository visitRepo;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired private IpdServiceRepository serviceRepo;
    @Autowired private IpdMedicationRepository medRepo;
    @Autowired private IpdAdmissionRepository admissionRepo;
    
 // ADD AUTOWIRED
    @Autowired
    private IpdTrackingService trackingService;


    // Admit patient
    @PostMapping("/admit")
    public ResponseEntity<IpdAdmission> admitPatient(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam Long roomId,
            @RequestParam String reason) {
        IpdAdmission admission = ipdService.admitPatient(patientId, doctorId, roomId, reason);
        return ResponseEntity.ok(admission);
    }


    // Genrate Bill patient
    @PostMapping("/generate-bill/{admissionId}")
    public ResponseEntity<IpdAdmission> generateBilling(@PathVariable Long admissionId) {
        IpdAdmission discharged = ipdService.generateBilling(admissionId);
        return ResponseEntity.ok(discharged);
    }
    
    
    //Update Billing
    @PostMapping("/regenerate-bill/{admissionId}")
    public ResponseEntity<String> regenerateBill(@PathVariable Long admissionId) {
        ipdService.regenerateBill(admissionId);
        return ResponseEntity.ok("Bill updated successfully for admission ID: " + admissionId);
    }
    
    @PostMapping("/visit/{admissionId}")
//    @PreAuthorize("hasRole('DOCTOR','NURSE', 'ADMIN')")
    public ResponseEntity<IpdDoctorVisit> addDoctorVisit(
            @PathVariable Long admissionId,
            @RequestParam Long doctorId,
            @RequestParam Double fee,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(trackingService.addDoctorVisit(admissionId, doctorId, fee, notes));
    }

    @PostMapping("/medication/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE' , 'ADMIN')")
    public ResponseEntity<IpdMedication> addMedication(
            @PathVariable Long admissionId,
            @RequestParam String medicineName,
            @RequestParam Integer quantity,
            @RequestParam Double pricePerUnit) {
        return ResponseEntity.ok(trackingService.addMedication(admissionId, medicineName, quantity, pricePerUnit));
    }

    @PostMapping("/service/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<IpdServiceRendered> addService(
            @PathVariable Long admissionId,
            @RequestParam String serviceType,
            @RequestParam String description,
            @RequestParam Double charge) {
        return ResponseEntity.ok(trackingService.addService(admissionId, serviceType, description, charge));
    }
    
 // ================= IPD TRACKING GET APIs =================

    @GetMapping("/visit/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE','ADMIN')")
    public ResponseEntity<List<IpdDoctorVisit>> getDoctorVisits(@PathVariable Long admissionId) {
        return ResponseEntity.ok(trackingService.getDoctorVisits(admissionId));
    }

    @GetMapping("/medication/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE','ADMIN')")
    public ResponseEntity<List<IpdMedication>> getMedications(@PathVariable Long admissionId) {
        return ResponseEntity.ok(trackingService.getMedications(admissionId));
    }

    @GetMapping("/service/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE','ADMIN')")
    public ResponseEntity<List<IpdServiceRendered>> getServices(@PathVariable Long admissionId) {
        return ResponseEntity.ok(trackingService.getServices(admissionId));
    }
    
    
    @GetMapping("/admissions/discharge")
    public ResponseEntity<List<IpdAdmission>> getDischargeAdmissions() {
        List<IpdAdmission> admissions = ipdService.getAllDischargeAdmissionsForHospital();
        return ResponseEntity.ok(admissions);
    }

    
    
    // IPD payment
    @PostMapping("/payment")
    public ResponseEntity<String> makePayment(@RequestBody IpdPaymentRequestDTO request) {	
        String result = ipdService.processPayment(request); 
        return ResponseEntity.ok(result);
    }
    
    //This Discharge API is only  
    @PostMapping("/discharge-patient/{admissionId}")
    public ResponseEntity<String> dischargeAfterPayment(@PathVariable Long admissionId){
    	ipdService.dischargeAfterPayment(admissionId);
    	return ResponseEntity.ok("Patient discharged successfully!");
    }
    

    
    @GetMapping("/billing-details/{admissionId}")
    public ResponseEntity<IpdBillingDetailsResponse> getBilling(@PathVariable Long admissionId) {
        IpdBillingDetailsResponse response = billingIntegrationService.getBillingDetails(admissionId);
        return ResponseEntity.ok(response);
    }


    // Get billing by admission ID
//    @GetMapping("/billing")
//    public ResponseEntity<IpdBilling> getBilling(@RequestParam Long admissionId) {
//        IpdBilling billing = ipdService.getBillingByAdmission(admissionId);
//        return ResponseEntity.ok(billing);
//    }
    
    @GetMapping("/billings")
    public ResponseEntity<List<IpdBilling>> getAllBillingForCurrentHospital() {
        List<IpdBilling> billings = ipdService.getAllBillingForCurrentHospital();
        return ResponseEntity.ok(billings);
    }
    
    @GetMapping("/billing/{billingId}/patient")
    public ResponseEntity<?> getPatientFromBilling(@PathVariable Long billingId) {
        IpdBilling billing = billingRepo.findById(billingId)
            .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));

        Long patientId = billing.getAdmission().getPatientId();
        
        Patient patient = patientRepository.findById(patientId).orElseThrow(()->new ResourceNotFoundException("Patient Not Found"));

        return ResponseEntity.ok(patient);
    }


    // Get all admissions for a hospital
    @GetMapping("/admissions")
    public ResponseEntity<List<IpdAdmission>> getAdmissions() {
        List<IpdAdmission> admissions = ipdService.getAllAdmissionsForHospital();
        return ResponseEntity.ok(admissions);
    }

    // Get available rooms
    @GetMapping("/rooms/available")
    public ResponseEntity<List<IpdRoom>> getAvailableRooms() {
        List<IpdRoom> rooms = ipdService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/rooms/availableid")
    public ResponseEntity<Long> getAvailableRoomId() {
        Long roomId = ipdService.findFirstAvailableRoomId();
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(roomId);
    }
    
    @GetMapping("/rooms/{roomNumber}")
    public ResponseEntity<IpdRoom> getRooms(@PathVariable String roomNumber) {
        IpdRoom rooms = ipdService.getRoomByRoomNumber(roomNumber);
        return ResponseEntity.ok(rooms);
    }

    // Create a new room
    @PostMapping("/rooms")
    public ResponseEntity<IpdRoom> createRoom(@RequestBody IpdRoom room) {
        IpdRoom savedRoom = ipdService.createRoom(room);
        return ResponseEntity.ok(savedRoom);
    }

    // Update a room
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<IpdRoom> updateRoom(@PathVariable Long roomId, @RequestBody IpdRoom updatedRoom) {
        IpdRoom room = ipdService.updateRoom(roomId, updatedRoom);
        return ResponseEntity.ok(room);
    }

    // Delete a room
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        ipdService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<IpdRoom> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(ipdService.getRoomById(roomId));
    }

  
  @GetMapping("/dashboard/summary")
  public ResponseEntity<IpdDashboardSummary> getSummary() {
      return ResponseEntity.ok(ipdService.getDashboardSummary());
  }

  @GetMapping("/dashboard/chart")
  public ResponseEntity<List<AdmissionChartPoint>> getChart(
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
  ) {
      return ResponseEntity.ok(ipdService.getAdmissionChart(from, to));
  }
  @PutMapping("/billing/pay/{admissionId}")
  public ResponseEntity<IpdBilling> payBill(
          @PathVariable Long admissionId,
          @RequestParam double amount
  ) {
      IpdBilling billing = ipdService.makePartialOrFullPayment(admissionId, amount);
      return ResponseEntity.ok(billing);
  }

  @PutMapping("/billing/add-doctor-fee/{admissionId}")
  public ResponseEntity<IpdBilling> addDoctorFee(
          @PathVariable Long admissionId,
          @RequestParam double fee
  ) {
      IpdBilling billing = ipdService.addAdditionalDoctorFee(admissionId, fee);
      return ResponseEntity.ok(billing);
  }
  
  // Enable/Disable IPD module for a hospital
  
  @PutMapping("/toggle-ipd/admin/{adminId}")
  @PreAuthorize("hasRole('SUPER_ADMIN')")
  public ResponseEntity<String> toggleIpdModuleForAdmin(
          @PathVariable Long adminId,
          @RequestParam boolean enabled
  ) {
      IpdModuleSetting setting = ipdModuleSettingRepo.findByAdminId(adminId)
              .orElseGet(() -> {
                  IpdModuleSetting s = new IpdModuleSetting();
                  s.setAdminId(adminId);
                  return s;
              });

      setting.setEnabled(enabled);
      ipdModuleSettingRepo.save(setting);

      return ResponseEntity.ok("IPD module for admin " + adminId + " is now " + (enabled ? "enabled" : "disabled") + ".");
  }
  
  @GetMapping("/check-access")
  public ResponseEntity<String> checkIpdAccessForHospital() {
      boolean isEnabled = ipdService.isIpdEnabledForCurrentHospital();

      if (isEnabled) {
          return ResponseEntity.ok("✅ IPD Module is ENABLED for your hospital.");
      } else {
          return ResponseEntity.ok("❌ IPD Module is DISABLED for your hospital.");
      }
  }
  
//New End Points for IPD Recommendations
  
  @PostMapping("/recommend")
  @PreAuthorize("hasRole('DOCTOR')")
  public ResponseEntity<IpdRecommendationResponseDTO> createRecommendation(@Valid @RequestBody IpdRecommendationCreateDTO dto){
	  return ResponseEntity.ok(ipdRecommendationService.createRecommendation(dto));
  }
  
  @GetMapping("/recommend/self")
  @PreAuthorize("hasRole('PATIENT')")
  public ResponseEntity<List<IpdRecommendationResponseDTO>> getPatientRecommendations() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      return ResponseEntity.ok(ipdRecommendationService.getRecommendationsByPatient(email));
  }

  @GetMapping("/recommend/doctor/self")
  @PreAuthorize("hasRole('DOCTOR')")
  public ResponseEntity<List<IpdRecommendationResponseDTO>> getDoctorRecommendations() {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      return ResponseEntity.ok(ipdRecommendationService.getRecommendationsByDoctor(email));
  }

  @PostMapping("/recommend/{recommendationId}/admit")
  @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
  public ResponseEntity<IpdAdmission> convertRecommendationToAdmission(
          @PathVariable Long recommendationId,
          @RequestParam Long roomId
  ) {
      return ResponseEntity.ok(ipdRecommendationService.convertToAdmission(recommendationId, roomId));
  }
  
  @GetMapping("/recommend/pending")
  @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
  public ResponseEntity<List<IpdRecommendationResponseDTO>> getPendingRecommendations() {
      User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
              .orElseThrow(() -> new ResourceNotFoundException("User not found"));
      
      IpdHospital hospital = hospitalRepository.findById(currentUser.getIpdHospitalId()).orElseThrow(()->new AccessDeniedException("User is not mapped to any hospital"));
      
      return ResponseEntity.ok(ipdRecommendationService.getPendingRecommendationsByHospital(hospital));
  }
  

  
}
