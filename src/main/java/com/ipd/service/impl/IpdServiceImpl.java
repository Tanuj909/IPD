package com.ipd.service.impl;

import com.ipd.entity.*;
import com.user.entity.Doctor;
import com.user.entity.Patient;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;
import com.user.repository.UserRepository;
import com.ipd.dto.AdmissionChartPoint;
import com.ipd.dto.IpdAdmissionUpdateRequest;
import com.ipd.dto.IpdBillRequestDTO;
import com.ipd.dto.IpdBillUpdateRequestDTO;
import com.ipd.dto.IpdDashboardSummary;
import com.ipd.dto.IpdPaymentRequestDTO;
import com.ipd.repository.*;
import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.service.DoctorVisitService;
import com.ipd.service.IpdService;
import com.ipd.service.IpdTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IpdServiceImpl implements IpdService {

    @Autowired
    private IpdAdmissionRepository ipdAdmissionRepo;

    @Autowired
    private IpdRoomRepository ipdRoomRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;
    
    @Autowired
    private IpdBillingRepository billingRepo;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IpdRecommendationRepository ipdRecommendationRepository;
    
    @Autowired
    private IpdHospitalRepository hospitalRepository;
    
    @Autowired
    private IpdHospitalPricingRepository pricingRepo;
    
    @Autowired
    private  DoctorVisitRepository doctorVisitRepo;
    @Autowired
    private  IpdServiceRepository serviceRepo;
    @Autowired
    private  IpdMedicationRepository medicationRepo;
    
    @Autowired
    private DoctorVisitService doctorVisitService;
    
    @Autowired
    private IpdBedRepository bedRepository;
    
    @Value("${billing.base.url}")   // <-- Inject value from application.properties
    private String billingBaseUrl;
    
 // ADD AUTOWIRED
    @Autowired
    private IpdTrackingService trackingService;
    
    @Autowired
    private RestTemplate restTemplate;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void checkAccess(IpdAdmission admission) {
        User currentUser = getCurrentUser();
        boolean isOwner = admission.getCreatedBy().equals(currentUser.getId());
        boolean isSameHospital = currentUser.getIpdHospitalId() != null && admission.getHospital() != null
                && currentUser.getIpdHospitalId().equals(admission.getHospital().getId());
        if (!(isOwner || isSameHospital)) {
            throw new AccessDeniedException("Unauthorized access to this patient");
        }
    }

    private void checkIpdModuleAccess() {
    	
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            return;
        }
        if (currentUser.getIpdHospitalId() == null) {
            throw new AccessDeniedException("IPD Module is DISABLED by SuperAdmin for this hospital.");
        }

//        User user = userRepository.findByRoleAndIpdHospitalId(Role.ADMIN, currentUser.getIpdHospitalId())
//                .orElseThrow(() -> new ResourceNotFoundException("Admin not found for this hospital."));
//
//        boolean allowed = ipdModuleSettingRepo.findByAdminId(user.getId())
//                .map(IpdModuleSetting::isEnabled)
//                .orElse(false);
//
//        if (!allowed) {
//            throw new AccessDeniedException("IPD Module is DISABLED by SuperAdmin for this hospital.");
//        }
    }


    
    
//    @Transactional
//    @Override
//    public IpdAdmission admitPatient(Long patientId, Long doctorId, Long roomId, String reason) {
//        checkIpdModuleAccess();
//        ipdAdmissionRepo.findByPatientIdAndIsDischargedFalse(patientId).ifPresent(a -> {
//            throw new IllegalStateException("Patient is already admitted and not discharged.");
//        });
//
//        Patient patient = patientRepo.findById(patientId).orElseThrow(()-> new ResourceNotFoundException("Patient not found"));
//        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(()-> new ResourceNotFoundException("Doctor not found"));
//        IpdRoom room = ipdRoomRepo.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found"));
//
//
//        if (room.getOccupiedBeds() >= room.getTotalBeds())
//            throw new IllegalStateException("No beds available in the selected room");
//
//        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
//        ipdRoomRepo.save(room);
//
//        IpdAdmission admission = new IpdAdmission();
//        admission.setPatientId(patient.getId());
//        admission.setDoctorId(doctor.getId());
//        admission.setRoom(room);
//        admission.setHospital(room.getHospital());
//        admission.setAdmissionDate(LocalDateTime.now());
//        admission.setDischarged(false);
//        admission.setReasonForAdmission(reason);
//        admission.setCreatedAt(LocalDateTime.now());
//        admission.setCreatedBy(getCurrentUser().getId());
//
//        IpdAdmission savedAdmission = ipdAdmissionRepo.save(admission);
//
//        // Calculate billing based on 1 day charge initially
//        double dailyRoomRate = room.getPrice();
//        double doctorFee = doctor.getConsultationFee();
//
//        IpdBilling billing = new IpdBilling();
//        billing.setAdmission(savedAdmission);
//        billing.setRoomCharges(dailyRoomRate); // 1st day room charge
//        billing.setDoctorFee(doctorFee);       // initial consultation fee
//        billing.setMiscellaneous(0);
//        billing.setDiscount(0);
//        billing.setDoctorVisitCount(1);
//        billing.setTotalAmount(dailyRoomRate + doctorFee);
//        billing.setFinalAmount(dailyRoomRate + doctorFee);
//        billing.setPaid(false);
//        billing.setGeneratedAt(LocalDateTime.now());
//
//        billingRepo.save(billing);
//        savedAdmission.setBilling(billing);
//
//        return ipdAdmissionRepo.save(savedAdmission);
//    }
    
    @Transactional
    @Override
    public IpdAdmission admitPatient(Long patientId, Long doctorId, Long roomId,Long ipdBedId, String reason
    		,Double advanceAmount,                    // ← Optional
            String advancePaymentMode) {

        checkIpdModuleAccess();

        ipdAdmissionRepo.findByPatientIdAndIsDischargedFalse(patientId).ifPresent(a -> {
            throw new IllegalStateException("Patient is already admitted and not discharged.");
        });

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        IpdRoom room = ipdRoomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // -------------------------------------------------------
        // ⭐ FIND FIRST FREE BED IN ROOM
        // -------------------------------------------------------
         room.getBeds().stream()
                .filter(b -> !b.isOccupied())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No beds available in this room"));

         IpdBed ipdBed = bedRepository.findById(ipdBedId).orElseThrow(()->new ResourceNotFoundException("Bed Not Available"));
        // Mark that bed as occupied
         ipdBed.setOccupied(true);

        // Update occupied count
        int occupiedCount = (int) room.getBeds().stream().filter(IpdBed::isOccupied).count();
        room.setOccupiedBeds(occupiedCount);
        ipdRoomRepo.save(room);

        // -------------------------------------------------------
        // ⭐ Create Admission
        // -------------------------------------------------------
        IpdAdmission admission = new IpdAdmission();
        admission.setPatientId(patient.getId());
        admission.setDoctorId(doctor.getId());
//        admission.setRoom(room);
        admission.setBed(ipdBed);  // <-- NEW: Assign bed
        admission.setHospital(room.getHospital());
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setDischarged(false);
        admission.setReasonForAdmission(reason);
        admission.setCreatedAt(LocalDateTime.now());
        admission.setCreatedBy(getCurrentUser().getId());

        IpdAdmission savedAdmission = ipdAdmissionRepo.save(admission);
        
        this.generateBilling(savedAdmission.getId(), advanceAmount, advancePaymentMode);


        return ipdAdmissionRepo.save(savedAdmission);
    }

    
    @Transactional
    @Override
    public IpdAdmission updateAdmissionFully(Long id, IpdAdmissionUpdateRequest request) {

        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        boolean updated = false;

        // -------------------------
        // ⭐ Update Doctor
        // -------------------------
        if (request.getDoctorId() != null) {
            Doctor doctor = doctorRepo.findById(request.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            admission.setDoctorId(doctor.getId());
            updated = true;
        }

        // -------------------------
        // ⭐ Update Room
        // -------------------------
        if (request.getRoomId() != null) {

            Long newRoomId = request.getRoomId();
            Long oldRoomId = admission.getBed().getRoom().getId();

            if (!newRoomId.equals(oldRoomId)) {

                IpdRoom oldRoom = admission.getBed().getRoom();
                IpdRoom newRoom = ipdRoomRepo.findById(newRoomId)
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

                // Free old bed
                IpdBed oldBed = admission.getBed();
                oldBed.setOccupied(false);

                // Find new free bed
                IpdBed freeBed = newRoom.getBeds().stream()
                        .filter(b -> !b.isOccupied())
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException("No empty bed available in new room"));

                freeBed.setOccupied(true);

                // Update room occupied counts
                oldRoom.setOccupiedBeds((int) oldRoom.getBeds().stream().filter(IpdBed::isOccupied).count());
                newRoom.setOccupiedBeds((int) newRoom.getBeds().stream().filter(IpdBed::isOccupied).count());

                ipdRoomRepo.save(oldRoom);
                ipdRoomRepo.save(newRoom);

//                admission.setRoom(newRoom);
                admission.setBed(freeBed);
                updated = true;
            }
        }

        // -------------------------
        // ⭐ Update Reason
        // -------------------------
        if (request.getReasonForAdmission() != null) {
            admission.setReasonForAdmission(request.getReasonForAdmission());
            updated = true;
        }

        if (updated) {
            admission.setUpdatedAt(LocalDateTime.now());
        }

        return ipdAdmissionRepo.save(admission);
    }

    

    @Override
    public List<IpdAdmission> getAllAdmissionsForCurrentHospital() {
        checkIpdModuleAccess();

        User currentUser = getCurrentUser();
        if (currentUser.getIpdHospitalId() == null) {
            throw new AccessDeniedException("User is not mapped to any IPD");
        }
        return ipdAdmissionRepo.findByHospitalId(currentUser.getIpdHospitalId());
    }

    @PostMapping("/visit/{admissionId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<IpdDoctorVisit> addDoctorVisit(
            @PathVariable Long admissionId,
            @RequestParam Long doctorId,
            @RequestParam Double fee,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(trackingService.addDoctorVisit(admissionId, doctorId, fee, notes));
    }

    @PostMapping("/medication/{admissionId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
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
    
    
    
    @Override
    @Transactional
    public IpdAdmission generateBilling(Long admissionId,Double advanceAmount, String advancePaymentMode) {
        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        checkAccess(admission);
        if (admission.isDischarged()) {
            throw new IllegalStateException("Patient is already discharged");
        }

        IpdHospitalPricing pricing = pricingRepo.findByHospital_Id(admission.getHospital().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pricing not set"));

        // === AGGREGATE FROM TRACKING TABLES ===
        List<IpdDoctorVisit> visits = doctorVisitRepo.findByAdmissionId(admissionId);
        List<IpdMedication> meds = medicationRepo.findByAdmissionId(admissionId);
        List<IpdServiceRendered> services = serviceRepo.findByAdmissionId(admissionId);

        long daysAdmitted = Math.max(1, ChronoUnit.DAYS.between(
                admission.getAdmissionDate().toLocalDate(), LocalDate.now()) + 1);

        // === ROOM CHARGES ===
      double roomCharges = admission.getBed().getRoom().getPrice() * daysAdmitted;
//        double roomCharges = admission.getBed().getBedNumber().


        // === DOCTOR VISITS ===
//        double doctorFees = visits.stream().mapToDouble(IpdDoctorVisit::getFee).sum();
//       long doctorId = admission.getDoctorId();
//       
//       Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(()-> new ResourceNotFoundException("Doctor not found"));
//       double doctorFee = doctor.getConsultationFee() * daysAdmitted;
        
     // NEW CORRECT WAY — uses actual recorded visits + counts
        double doctorFee = doctorVisitService.calculateTotalDoctorFees(admissionId) * daysAdmitted;
       

        // === MEDICATIONS (from tracking) ===
        double medicationCharges = meds.stream()
                .mapToDouble(m -> m.getQuantity() * m.getPricePerUnit())
                .sum();

        // === DAILY FIXED FEES (from IpdHospitalPricing) ===
        double dailyNursing = pricing.getNursingFee() * daysAdmitted;
        double dailyFood = pricing.getFoodFee() * daysAdmitted;
        double dailyDiagnostic = pricing.getDiagnosticFee() * daysAdmitted;
        double dailyMisc = pricing.getMiscellaneousFee() * daysAdmitted;

        // === ADDITIONAL ONE-TIME SERVICES (from IpdServiceRendered) ===
        double extraNursing = services.stream()
                .filter(s -> "NURSING".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge)
                .sum();

        double extraDiagnostic = services.stream()
                .filter(s -> "DIAGNOSTIC".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge)
                .sum();

        double extraProcedure = services.stream()
                .filter(s -> "PROCEDURE".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge)
                .sum();

        double extraFood = services.stream()
                .filter(s -> "FOOD".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge)
                .sum();

        double extraMisc = services.stream()
                .filter(s -> "MISC".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge)
                .sum();

        // === FINAL CHARGES ===
        double nursingCharges = dailyNursing + extraNursing;
        double foodCharges = dailyFood + extraFood;
        double diagnosticCharges = dailyDiagnostic + extraDiagnostic;
        double miscellaneousCharges = dailyMisc + extraMisc;
        double procedureCharges = extraProcedure; // Usually not daily

        // === BUILD DTO ===
        IpdBillRequestDTO request = new IpdBillRequestDTO();
        request.setPatientExternalId(admission.getPatientId());
        request.setHospitalExternalId(admission.getHospital().getId());
        request.setAdmissionId(admissionId);
        request.setAdmissionDate(admission.getAdmissionDate().toLocalDate());
        request.setDischargeDate(LocalDate.now());
        request.setRoomRatePerDay(admission.getBed().getRoom().getPrice());
        request.setMedicationCharges(medicationCharges);
        request.setNursingCharges(nursingCharges);
//        request.setDoctorFee(doctorFees);
        request.setDoctorFee(doctorFee);
        request.setDiagnosticCharges(pricing.getDiagnosticFee());
        request.setProcedureCharges(procedureCharges);
        request.setFoodCharges(foodCharges);
        request.setMiscellaneousCharges(miscellaneousCharges);
        request.setDiscountPercentage(pricing.getDiscountPercentage());
        request.setGstPercentage(pricing.getGstPercentage());
        request.setAdvanceAmount(advanceAmount);
        request.setAdvancePaymentMode(advancePaymentMode);
        
        System.out.println(pricing.getDiscountPercentage());
 
        // === CALL BILLING API ===
        String url = billingBaseUrl + "ipd/generate-bill" ;
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        System.out.println("Billing API Response: " + response.getBody());

        return admission;
    }
    
    @Override
    @Transactional
    public void regenerateBill(Long admissionId) {
        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        if (admission.isDischarged()) {
            throw new IllegalStateException("Cannot regenerate bill for discharged patient");
        }

        IpdHospitalPricing pricing = pricingRepo.findByHospital_Id(admission.getHospital().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital pricing not configured"));

        LocalDate admissionDate = admission.getAdmissionDate().toLocalDate();
        LocalDate today = LocalDate.now();
        long daysAdmitted = Math.max(1, ChronoUnit.DAYS.between(admissionDate, today) + 1);

        // === RECALCULATE EVERYTHING FRESH FROM TRACKING TABLES ===
        List<IpdMedication> meds = medicationRepo.findByAdmissionId(admissionId);
        List<IpdServiceRendered> services = serviceRepo.findByAdmissionId(admissionId);

//        Doctor doctor = doctorRepo.findById(admission.getDoctorId())
//                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // Daily charges
        double roomCharges = admission.getBed().getRoom().getPrice() * daysAdmitted;
//        double doctorFee = doctor.getConsultationFee() * daysAdmitted;
        
     // === DOCTOR VISITS (NEW ACCURATE WAY) ===
        double doctorFee = doctorVisitService.calculateTotalDoctorFees(admissionId) * daysAdmitted;

        double dailyNursing = pricing.getNursingFee() * daysAdmitted;
        double dailyFood = pricing.getFoodFee() * daysAdmitted;
        double dailyDiagnostic = pricing.getDiagnosticFee() * daysAdmitted;
        double dailyMisc = pricing.getMiscellaneousFee() * daysAdmitted;

        // Extra one-time services
        double extraNursing = services.stream()
                .filter(s -> "NURSING".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge).sum();

        double extraDiagnostic = services.stream()
                .filter(s -> "DIAGNOSTIC".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge).sum();

        double extraProcedure = services.stream()
                .filter(s -> "PROCEDURE".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge).sum();

        double extraFood = services.stream()
                .filter(s -> "FOOD".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge).sum();

        double extraMisc = services.stream()
                .filter(s -> "MISC".equalsIgnoreCase(s.getServiceType()))
                .mapToDouble(IpdServiceRendered::getCharge).sum();

        double medicationCharges = meds.stream()
                .mapToDouble(m -> m.getQuantity() * m.getPricePerUnit())
                .sum();

        // === BUILD FULL UPDATE REQUEST (Same structure as generate) ===
        IpdBillUpdateRequestDTO request = new IpdBillUpdateRequestDTO();
        request.setAdmissionId(admissionId);
        request.setPatientExternalId(admission.getPatientId());
        request.setHospitalExternalId(admission.getHospital().getId());
        request.setAdmissionDate(admissionDate);
        request.setDischargeDate(today);

        request.setRoomRatePerDay(admission.getBed().getRoom().getPrice());

        // Send TOTAL charges (not per day!)
        request.setNursingChargesPerDay(pricing.getNursingFee());
        request.setFoodChargesPerDay(pricing.getFoodFee());
        request.setDiagnosticChargesPerDay(pricing.getDiagnosticFee());
        request.setMiscChargesPerDay(pricing.getMiscellaneousFee());

        // Send CURRENT accumulated values
        request.setMedicationCharges(medicationCharges);
        request.setDoctorFee(doctorFee);                    // Now scales with days!
        request.setProcedureCharges(extraProcedure);
        request.setExtraServiceCharges(extraNursing + extraFood + extraDiagnostic + extraMisc);

        request.setDiscountPercentage(pricing.getDiscountPercentage());
        request.setGstPercentage(pricing.getGstPercentage());

        // Call Billing Module
        String url = billingBaseUrl + "ipd/update-bill";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IpdBillUpdateRequestDTO> entity = new HttpEntity<>(request, headers);

        restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
    }
    
    //This method is called by the Billing module
//    @Transactional
//    @Override
//    public IpdAdmission generateBilling(Long admissionId) {
//        checkIpdModuleAccess();
//
//        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + admissionId));
//
//        checkAccess(admission);
//        
//        if (admission.isDischarged()) {
//            throw new IllegalStateException("Patient is already discharged");
//        }
//        
//        IpdHospitalPricing pricing = pricingRepo.findByHospital_Id(admission.getHospital().getId())
//        		.orElseThrow(()-> new ResourceNotFoundException("Pricing not set for hospital"));
//
//        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for admission"));
//        
////        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(()-> new ResourceNotFoundException("Doctor not found"));
//
////        if (!billing.isPaid()) {
////            double pendingAmount = billing.getFinalAmount();
////            throw new IllegalStateException("Cannot discharge. Amount is pending: ₹" + pendingAmount);
////        }
//        
//        // ✅ Step 1: Prepare Billing Request
//        IpdBillRequestDTO request = new IpdBillRequestDTO();
//        request.setPatientExternalId(admission.getPatientId());
//        request.setHospitalExternalId(admission.getHospital().getId());
//        request.setAdmissionId(admissionId);
//        request.setAdmissionDate(admission.getAdmissionDate().toLocalDate());
//        request.setDischargeDate(LocalDate.now());
//        request.setRoomRatePerDay(admission.getRoom().getPrice());
//        request.setMedicationCharges(pricing.getMedicationFee()); // Example default or calculated
//        request.setNursingCharges(pricing.getNursingFee());
////        request.setDoctorFee(admission.getDoctorId().getConsultationFee());
//        request.setDoctorFee(1000);
//        request.setDiagnosticCharges(pricing.getDiagnosticFee());
//        request.setFoodCharges(pricing.getFoodFee());
//        request.setMiscellaneousCharges(pricing.getMiscellaneousFee());
//        request.setPaymentStatus(request.getPaymentStatus());
//
//        // ✅ Step 2: Call Billing Service API
////        RestTemplate restTemplate = new RestTemplate();
//        String billingApiUrl = "http://147.93.28.8:3005/api/billing/ipd/generate-bill";
//        ResponseEntity<String> billingResponse =
//                restTemplate.postForEntity(billingApiUrl, request, String.class);
//        System.out.println("Billing generated: " + billingResponse.getBody());
//        return ipdAdmissionRepo.save(admission);
//    }
//    

    //---------------------------------
    @Override
    public String processPayment(IpdPaymentRequestDTO request) {	
    	String billingApiUrl = billingBaseUrl+ "ipd/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<IpdPaymentRequestDTO> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(billingApiUrl, entity, String.class);

        return response.getBody();
    }
    
    //This method will call the billing API to check if the payment is done or not!
//    @Transactional
//    @Override
//    public void dischargeAfterPayment(Long admissionId) {
//    	 checkIpdModuleAccess();
//         IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + admissionId));
//         
//         checkAccess(admission);
//         if (admission.isDischarged()) {
//             throw new IllegalStateException("Patient is already discharged");
//         }
//         
//         // ✅ Step 1: Verify Payment from Billing Module before discharging
//         
//         String billingApiUrl = billingBaseUrl + "ipd/status?admissionId=" + admissionId;      
//         
//         ResponseEntity<String> response = restTemplate.getForEntity(billingApiUrl, String.class);
//         
//         if (!response.getBody().equalsIgnoreCase("PAID")) {
//             throw new IllegalStateException("Cannot discharge. Payment still pending!");
//         }
//
//         // ✅ Step 2: Mark patient as discharged
//         admission.setDischarged(true);
//         admission.setDischargeDate(LocalDateTime.now());
//         
//         // ✅ Step 3: Update Room availability
//         IpdRoom room = admission.getRoom();
//         room.setOccupiedBeds(room.getOccupiedBeds() - 1);
//         ipdRoomRepo.save(room);
//         
//         ipdAdmissionRepo.save(admission);
//    }
    
    @Transactional
    @Override
    public void dischargeAfterPayment(Long admissionId) {

        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found"));

        checkAccess(admission);

        if (admission.isDischarged()) {
            throw new IllegalStateException("Patient is already discharged");
        }

        // -------------------------
        // ⭐ Check Billing Status
        // -------------------------
        String url = billingBaseUrl + "ipd/status?admissionId=" + admissionId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getBody().equalsIgnoreCase("PAID")) {
            throw new IllegalStateException("Cannot discharge. Payment still pending!");
        }

        // -------------------------
        // ⭐ Mark patient as discharged
        // -------------------------
        admission.setDischarged(true);
        admission.setDischargeDate(LocalDateTime.now());

        // -------------------------
        // ⭐ Free Bed
        // -------------------------
        IpdRoom room = admission.getBed().getRoom();
        
        IpdBed bed = admission.getBed();
        bed.setOccupied(false);

        room.setOccupiedBeds((int) room.getBeds().stream().filter(IpdBed::isOccupied).count());
        ipdRoomRepo.save(room);

        ipdAdmissionRepo.save(admission);
    }

    
    @Override
    public List<IpdAdmission> getAllDischargeAdmissionsForHospital() {
        checkIpdModuleAccess();
 
        IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId()).orElseThrow();
        List<IpdAdmission> allAdmissions = ipdAdmissionRepo.findByHospitalId(hospital.getId());
 
        List<IpdAdmission> activeAdmissions = new ArrayList<>();
        for (IpdAdmission admission : allAdmissions) {
            if (admission.isDischarged()) {
                activeAdmissions.add(admission);
            }
        }
        return activeAdmissions;
    }
//------------------------------
    @Override
    public IpdBilling getBillingByAdmission(Long admissionId) {
        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId).orElseThrow();
        checkAccess(admission);

        return billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));
    }

    
//-------------------------------
    @Override
    public List<IpdBilling> getAllBillingForCurrentHospital() {
        checkIpdModuleAccess();

        User currentUser = getCurrentUser();
        if (currentUser.getIpdHospitalId() == null) {
            throw new AccessDeniedException("User is not mapped to any hospital");
        }

        Long hospitalId = currentUser.getIpdHospitalId();
        return billingRepo.findByHospital_HospitalId(hospitalId);
    }

    @Override
    public List<IpdAdmission> getAllAdmissionsForHospital() {
        checkIpdModuleAccess();

        IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId()).orElseThrow();
        List<IpdAdmission> allAdmissions = ipdAdmissionRepo.findByHospitalId(hospital.getId());

        List<IpdAdmission> activeAdmissions = new ArrayList<>();
        for (IpdAdmission admission : allAdmissions) {
            if (!admission.isDischarged()) {
                activeAdmissions.add(admission);
            }
        }
        return activeAdmissions;
    }

    @Override
    public List<IpdRoom> getAvailableRooms() {
        checkIpdModuleAccess();

        Long hospitalId = getCurrentUser().getIpdHospitalId();
        return ipdRoomRepo.findAvailableRoomsByHospital(hospitalId);
    }
    
    @Override
    public Long findFirstAvailableRoomId() {
        //  Get all available rooms for current hospital
        List<IpdRoom> availableRooms = getAvailableRooms();
 
        //  If none available, return null
        if (availableRooms == null || availableRooms.isEmpty()) {
            return null;
        }
 
        // Return first available room’s ID
        return availableRooms.get(0).getId();
    }

    @Override
    public IpdRoom createRoom(IpdRoom room) {
        checkIpdModuleAccess();        
        try {
        	IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId()).orElseThrow(()->new ResourceNotFoundException("Hospital Not Found"));
            
            room.setOccupiedBeds(0);
            room.setActive(true);
            room.setHospital(hospital);
            
		} catch (Exception e) {
			System.out.println("New Print"+e);
		}
        return ipdRoomRepo.save(room);
    }

    @Override
    public IpdRoom getRoomByRoomNumber(String roomNumber) {
        checkIpdModuleAccess();

        User currentUser = getCurrentUser();

        IpdRoom room = ipdRoomRepo.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with number: " + roomNumber));

        if (currentUser.getIpdHospitalId() == null || room.getHospital() == null ||
                !currentUser.getIpdHospitalId().equals(room.getHospital().getId())) {
            throw new AccessDeniedException("Unauthorized to access this room");
        }

        return room;
    }

    @Override
    public IpdRoom getRoomById(Long roomId) {
        checkIpdModuleAccess();

        IpdRoom room = ipdRoomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        User currentUser = getCurrentUser();
        if (currentUser.getIpdHospitalId() == null ||
                !room.getHospital().getId().equals(currentUser.getIpdHospitalId())) {
            throw new AccessDeniedException("Unauthorized access to this room");
        }

        return room;
    }

    @Override
    @Transactional
    public IpdRoom updateRoom(Long roomId, IpdRoom updatedRoom) {
        checkIpdModuleAccess();

        IpdRoom room = ipdRoomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        room.setRoomNumber(updatedRoom.getRoomNumber());
        room.setTotalBeds(updatedRoom.getTotalBeds());
        room.setActive(updatedRoom.isActive());

        return ipdRoomRepo.save(room);
    }

    @Override
    public void deleteRoom(Long roomId) {
        checkIpdModuleAccess();
        ipdRoomRepo.deleteById(roomId);
    }

    @Override
    public IpdDashboardSummary getDashboardSummary() {
        checkIpdModuleAccess();

        IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId()).orElseThrow();

        long admittedPatients = ipdAdmissionRepo.countByHospitalAndIsDischargedFalse(hospital);
        List<IpdRoom> rooms = ipdRoomRepo.findByHospital(hospital);

        long totalRooms = rooms.size();
        long totalBeds = rooms.stream().mapToLong(IpdRoom::getTotalBeds).sum();
        long occupiedBeds = rooms.stream().mapToLong(IpdRoom::getOccupiedBeds).sum();
        long availableBeds = totalBeds - occupiedBeds;

        IpdDashboardSummary summary = new IpdDashboardSummary();
        summary.setTotalAdmittedPatients(admittedPatients);
        summary.setTotalRooms(totalRooms);
        summary.setTotalBeds(totalBeds);
        summary.setOccupiedBeds(occupiedBeds);
        summary.setAvailableBeds(availableBeds);

        return summary;
    }

    @Override
    public List<AdmissionChartPoint> getAdmissionChart(LocalDateTime from, LocalDateTime to) {
        checkIpdModuleAccess();

        User currentUser = getCurrentUser();
        IpdHospital hospital = hospitalRepository.findById(getCurrentUser().getIpdHospitalId()).orElseThrow();

        List<Object[]> rawData = ipdAdmissionRepo.countAdmissionsByDateRange(hospital.getId(), from, to);

        return rawData.stream()
                .map(row -> new AdmissionChartPoint((LocalDateTime) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    @Override
    public IpdBilling makePartialOrFullPayment(Long admissionId, double amount) {
        checkIpdModuleAccess();

        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));

        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0");

        double finalAmount = billing.getFinalAmount() - amount;
        billing.setFinalAmount(finalAmount);

        if (finalAmount <= 0) {
            billing.setPaid(true);
            billing.setFinalAmount(0);
            billing.setPaidAt(LocalDateTime.now());
        }

        return billingRepo.save(billing);
    }

    @Override
    public IpdBilling addAdditionalDoctorFee(Long admissionId, double fee) {
        checkIpdModuleAccess();

        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));

        billing.setDoctorFee(billing.getDoctorFee() + fee);
        billing.setTotalAmount(billing.getTotalAmount() + fee);
        billing.setFinalAmount(billing.getFinalAmount() + fee);

        return billingRepo.save(billing);
    }

    // ✅ Check if IPD is enabled for current hospital
    @Override
    public boolean isIpdEnabledForCurrentHospital() {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            return true;
        }

        if (currentUser.getIpdHospitalId() == null) {
            throw new AccessDeniedException("User is not mapped to any hospital.");
        }else {
        	return true;
        }

//        User admin = userRepository.findByRoleAndIpdHospitalId(Role.ADMIN, currentUser.getIpdHospitalId())
//                .orElseThrow(() -> new ResourceNotFoundException("Admin not found for this hospital."));

//        return ipdModuleSettingRepo.findByAdminId(admin.getId())
//                .map(IpdModuleSetting::isEnabled)
//                .orElse(false);
    }
    
//    @Override
//    @Transactional
//    public IpdAdmission admitFromRecommendation(Long recommendationId, Long roomId, Long bedId, String reason) {
//        checkIpdModuleAccess();
//        return admitPatient(
//                ipdRecommendationRepository.findById(recommendationId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with ID: " + recommendationId))
//                        .getPatientId(),
//                ipdRecommendationRepository.findById(recommendationId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with ID: " + recommendationId))
//                        .getDoctorId(),
//                roomId,
//                bedId,
//                reason
//        );
//    }
//    
}
