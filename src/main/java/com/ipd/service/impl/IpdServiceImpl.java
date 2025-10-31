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
import com.ipd.dto.IpdBillRequestDTO;
import com.ipd.dto.IpdDashboardSummary;
import com.ipd.dto.IpdPaymentRequestDTO;
import com.ipd.repository.*;
import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.service.BillingIntegrationService;
import com.ipd.service.IpdService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private IpdModuleSettingRepository ipdModuleSettingRepo;
    
    @Autowired
    private IpdRecommendationRepository ipdRecommendationRepository;
    
    @Autowired
    private IpdHospitalRepository hospitalRepository;
    
    @Autowired
    private IpdHospitalPricingRepository pricingRepo;
    

    
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
//
//        ipdAdmissionRepo.findByPatient_PatientIdAndIsDischargedFalse(patientId).ifPresent(a -> {
//            throw new IllegalStateException("Patient is already admitted and not discharged.");
//        });
//
//        Patient patient = patientRepo.findById(patientId).orElseThrow();
//        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
//        IpdRoom room = ipdRoomRepo.findById(roomId).orElseThrow();
//
//        if (room.getOccupiedBeds() >= room.getTotalBeds()) {
//            throw new IllegalStateException("No beds available in the selected room");
//        }
//
//        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
//        ipdRoomRepo.save(room);
//
//        IpdAdmission admission = new IpdAdmission();
//        admission.setPatient(patient);
//        admission.setDoctor(doctor);
//        admission.setRoom(room);
//        admission.setHospital(room.getHospital());
//        admission.setAdmissionDate(LocalDateTime.now());
//        admission.setDischarged(false);
//        admission.setReasonForAdmission(reason);
//        admission.setCreatedAt(LocalDateTime.now());
//        admission.setCreatedBy(getCurrentUser());
//
//        IpdAdmission savedAdmission = ipdAdmissionRepo.save(admission);
//
//        IpdBilling billing = new IpdBilling();
//        billing.setAdmission(savedAdmission);
//        billing.setRoomCharges(room.getPrice());
//        billing.setDoctorFee(doctor.getConsultationFee());
//        billing.setMiscellaneous(0);
//        billing.setDiscount(0);
//        double total = billing.getRoomCharges() + billing.getDoctorFee();
//        billing.setTotalAmount(total);
//        billing.setFinalAmount(total);
//        billing.setPaid(false);
//        billing.setGeneratedAt(LocalDateTime.now());
//
//        billingRepo.save(billing);
//
//        savedAdmission.setBilling(billing);
//        return ipdAdmissionRepo.save(savedAdmission);
//    }
    
    
    @Transactional
    @Override
    public IpdAdmission admitPatient(Long patientId, Long doctorId, Long roomId, String reason) {
        checkIpdModuleAccess();
        ipdAdmissionRepo.findByPatientIdAndIsDischargedFalse(patientId).ifPresent(a -> {
            throw new IllegalStateException("Patient is already admitted and not discharged.");
        });

        Patient patient = patientRepo.findById(patientId).orElseThrow(()-> new ResourceNotFoundException("Patient not found"));
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(()-> new ResourceNotFoundException("Doctor not found"));
        IpdRoom room = ipdRoomRepo.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found"));


        if (room.getOccupiedBeds() >= room.getTotalBeds())
            throw new IllegalStateException("No beds available in the selected room");

        room.setOccupiedBeds(room.getOccupiedBeds() + 1);
        ipdRoomRepo.save(room);

        IpdAdmission admission = new IpdAdmission();
        admission.setPatientId(patient.getId());
        admission.setDoctorId(doctor.getId());
        admission.setRoom(room);
        admission.setHospital(room.getHospital());
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setDischarged(false);
        admission.setReasonForAdmission(reason);
        admission.setCreatedAt(LocalDateTime.now());
        admission.setCreatedBy(getCurrentUser().getId());

        IpdAdmission savedAdmission = ipdAdmissionRepo.save(admission);

        // Calculate billing based on 1 day charge initially
        double dailyRoomRate = room.getPrice();
        double doctorFee = doctor.getConsultationFee();

        IpdBilling billing = new IpdBilling();
        billing.setAdmission(savedAdmission);
        billing.setRoomCharges(dailyRoomRate); // 1st day room charge
        billing.setDoctorFee(doctorFee);       // initial consultation fee
        billing.setMiscellaneous(0);
        billing.setDiscount(0);
        billing.setDoctorVisitCount(1);
        billing.setTotalAmount(dailyRoomRate + doctorFee);
        billing.setFinalAmount(dailyRoomRate + doctorFee);
        billing.setPaid(false);
        billing.setGeneratedAt(LocalDateTime.now());

        billingRepo.save(billing);
        savedAdmission.setBilling(billing);

        return ipdAdmissionRepo.save(savedAdmission);
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

//    @Transactional
//    @Override
//    public IpdAdmission dischargePatient(Long admissionId) {
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
//        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for admission"));
//
//        if (!billing.isPaid()) {
//            double pendingAmount = billing.getFinalAmount();
//            throw new IllegalStateException("Cannot discharge. Amount is pending: ₹" + pendingAmount);
//        }
//
//        admission.setDischarged(true);
//        admission.setDischargeDate(LocalDateTime.now());
//
//        IpdRoom room = admission.getRoom();
//        room.setOccupiedBeds(room.getOccupiedBeds() - 1);
//        ipdRoomRepo.save(room);
//
//        return ipdAdmissionRepo.save(admission);
//    }
    
    //This method is called by the Billing module
    @Transactional
    @Override
    public IpdAdmission generateBilling(Long admissionId) {
        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + admissionId));

        checkAccess(admission);

        if (admission.isDischarged()) {
            throw new IllegalStateException("Patient is already discharged");
        }
        
        IpdHospitalPricing pricing = pricingRepo.findByHospital_Id(admission.getHospital().getId())
        		.orElseThrow(()-> new ResourceNotFoundException("Pricing not set for hospital"));

        IpdBilling billing = billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for admission"));

//        if (!billing.isPaid()) {
//            double pendingAmount = billing.getFinalAmount();
//            throw new IllegalStateException("Cannot discharge. Amount is pending: ₹" + pendingAmount);
//        }
        
       
        // ✅ Step 1: Prepare Billing Request
        IpdBillRequestDTO request = new IpdBillRequestDTO();
        request.setPatientExternalId(admission.getPatientId());
        request.setHospitalExternalId(admission.getHospital().getId());
        request.setAdmissionId(admissionId);
        request.setAdmissionDate(admission.getAdmissionDate().toLocalDate());
        request.setDischargeDate(LocalDate.now());
        request.setRoomRatePerDay(admission.getRoom().getPrice());
        request.setMedicationCharges(pricing.getMedicationFee()); // Example default or calculated
        request.setNursingCharges(pricing.getNursingFee());
//        request.setDoctorFee(admission.getDoctorId().getConsultationFee());
        request.setDoctorFee(1000);
        request.setDiagnosticCharges(pricing.getDiagnosticFee());
        request.setFoodCharges(pricing.getFoodFee());
        request.setMiscellaneousCharges(pricing.getMiscellaneousFee());
        request.setPaymentStatus(request.getPaymentStatus());

        // ✅ Step 2: Call Billing Service API
//        RestTemplate restTemplate = new RestTemplate();
        String billingApiUrl = "http://localhost:8282/api/billing/ipd/generate-bill";

        ResponseEntity<String> billingResponse =
                restTemplate.postForEntity(billingApiUrl, request, String.class);

        System.out.println("Billing generated: " + billingResponse.getBody());

//        if() {
//        	
//        }
        // ✅ Step 3: Update Admission
//        admission.setDischarged(true);
//        admission.setDischargeDate(LocalDateTime.now());
//
//        IpdRoom room = admission.getRoom();
//        room.setOccupiedBeds(room.getOccupiedBeds() - 1);
//        ipdRoomRepo.save(room);

        return ipdAdmissionRepo.save(admission);
    }
    
    //This method will call the billing API to check if the payment is done or not!
    @Transactional
    @Override
    public void dischargeAfterPayment(Long admissionId) {
    	 checkIpdModuleAccess();
    	 
         IpdAdmission admission = ipdAdmissionRepo.findById(admissionId)
                 .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + admissionId));
         
         checkAccess(admission);
         

         if (admission.isDischarged()) {
             throw new IllegalStateException("Patient is already discharged");
         }
         
         // ✅ Step 1: Verify Payment from Billing Module before discharging
         String billingApiUrl = "http://localhost:8282/api/billing/ipd/status?admissionId=" + admissionId;
         
         ResponseEntity<String> response = restTemplate.getForEntity(billingApiUrl, String.class);
         
         if (!response.getBody().equalsIgnoreCase("PAID")) {
             throw new IllegalStateException("Cannot discharge. Payment still pending!");
         }

         // ✅ Step 2: Mark patient as discharged
         admission.setDischarged(true);
         admission.setDischargeDate(LocalDateTime.now());
         
         // ✅ Step 3: Update Room availability
         IpdRoom room = admission.getRoom();
         room.setOccupiedBeds(room.getOccupiedBeds() - 1);
         ipdRoomRepo.save(room);
         
         ipdAdmissionRepo.save(admission);
    }
    
    @Override
    public String processPayment(IpdPaymentRequestDTO request) {
        String billingApiUrl = "http://localhost:8282/api/billing/ipd/payment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<IpdPaymentRequestDTO> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(billingApiUrl, entity, String.class);

        return response.getBody();
    }


    @Override
    public IpdBilling getBillingByAdmission(Long admissionId) {
        checkIpdModuleAccess();

        IpdAdmission admission = ipdAdmissionRepo.findById(admissionId).orElseThrow();
        checkAccess(admission);

        return billingRepo.findByAdmissionId(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found"));
    }

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
    
    @Override
    @Transactional
    public IpdAdmission admitFromRecommendation(Long recommendationId, Long roomId, String reason) {
        checkIpdModuleAccess();
        return admitPatient(
                ipdRecommendationRepository.findById(recommendationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with ID: " + recommendationId))
                        .getPatientId(),
                ipdRecommendationRepository.findById(recommendationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found with ID: " + recommendationId))
                        .getDoctorId(),
                roomId,
                reason
        );
    }
    

}
