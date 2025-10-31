package com.ipd.controller;

import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdHospital;
import com.user.entity.Admin;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.AdminRepository;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;
import com.user.repository.UserRepository;
import com.ipd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class IpdDashboardController {

    @Autowired private DoctorRepository doctorRepo;
    @Autowired private PatientRepository patientRepo;
//    @Autowired private AppointmentRepository appointmentRepo;
//    @Autowired private OPDVisitRepository opdVisitRepo;
    @Autowired private UserRepository userRepo;
    
    @Autowired 
    private IpdHospitalRepository hospitalRepo;
    
    @Autowired 
    private AdminRepository adminRepo;

    private User getCurrentUser() {
        return userRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow();
    }

    private IpdHospital getHospital() {
    	IpdHospital hospital = hospitalRepo.findById(getCurrentUser().getIpdHospitalId()).orElseThrow(()->new ResourceNotFoundException("Hospital not Present for this User"));
        return hospital;
    }
    
    private Admin getAdminl() {
    	Admin admin;
    	if (getCurrentUser().getRole()==Role.ADMIN) {
    		 admin = adminRepo.findById(getCurrentUser().getAdmin().getId()).orElseThrow(()->new ResourceNotFoundException("Hospital not Present for this User"));
		}else if(getCurrentUser().getRole()==Role.DOCTOR) {
			 admin = adminRepo.findById(getCurrentUser().getDoctor().getAdmin().getId()).orElseThrow(()->new ResourceNotFoundException("Hospital not Present for this User"));
		}else {
			 admin = adminRepo.findById(getCurrentUser().getStaff().getAdmin().getId()).orElseThrow(()->new ResourceNotFoundException("Hospital not Present for this User"));
		}
        return admin;
    }

    // ✅ Dynamic Stats API
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        IpdHospital hospital = getHospital();

        LocalDateTime startDate = (start != null) ? start : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = (end != null) ? end : LocalDateTime.now();

        long patientCount = patientRepo.countByAdminAndCreatedAtBetween(
                getAdminl(), startDate, endDate);

        long doctorCount = doctorRepo.countByAdminAndJoiningDateBetween(
                getAdminl(), startDate, endDate);

        List<Map<String, Object>> stats = List.of(
                Map.of("title", "Patients", "count", patientCount),
                Map.of("title", "Doctors", "count", doctorCount)
        );

        return ResponseEntity.ok(stats);
    }


    // ✅ Line Chart - Monthly/Daily/Weekly
    @GetMapping("/patient-trend")
    public ResponseEntity<?> getPatientTrend(
            @RequestParam(defaultValue = "monthly") String type
    ) {
        IpdHospital hospital = getHospital();
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        if (type.equalsIgnoreCase("monthly")) {
            for (int month = 1; month <= 12; month++) {
//                int count = patientRepo.countByHospitalAndMonth(hospital.getId(), month);
            	int count = patientRepo.countByAdminAndMonth(getAdminl().getId(), month);
                labels.add(getMonthName(month));
                data.add(count);
            }
//        } else if (type.equalsIgnoreCase("weekly")) {
//            LocalDate today = LocalDate.now();
//            for (int i = 6; i >= 0; i--) {
//                LocalDate date = today.minusDays(i);
//                int count = opdVisitRepo.countByHospitalAndVisitDate(hospital, Date.valueOf(date));
//                labels.add(date.getDayOfWeek().toString());
//                data.add(count);
//            }
        } else if (type.equalsIgnoreCase("daily")) {
            LocalDate today = LocalDate.now();
            for (int i = 29; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                int count = patientRepo.countByAdminAndCreatedAt(getAdminl(), java.sql.Date.valueOf(date));
                labels.add(date.toString());
                data.add(count);
            }
        }

        Map<String, Object> response = Map.of(
                "labels", labels,
                "data", data
        );

        return ResponseEntity.ok(response);
    }

    // ✅ Doctor Specialization Breakdown - Dynamic
    @GetMapping("/doctor-specialization")
    public ResponseEntity<?> getDoctorSpecialization(
            @RequestParam(required = false) String specialization
    ) {
        IpdHospital hospital = getHospital();
        List<Object[]> result = doctorRepo.countBySpecializationGroupByAdmin(getAdminl());

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Object[] obj : result) {
            String spec = (String) obj[0];
            if (specialization == null || spec.equalsIgnoreCase(specialization)) {
                labels.add(spec);
                data.add((Long) obj[1]);
            }
        }

        return ResponseEntity.ok(Map.of(
                "labels", labels,
                "data", data
        ));
    }

    // ✅ Bed Occupancy Map - Dynamic Example
    @GetMapping("/bed-occupancy")
    public ResponseEntity<?> getBedOccupancy() {
        List<Map<String, Object>> data = List.of(
                Map.of("hospitalName", getHospital().getName(), "totalBeds", 100, "occupiedBeds", 80, "lat", 28.6139, "lng", 77.2090)
        );
        return ResponseEntity.ok(data);
    }

    // ✅ Slots Summary
    @GetMapping("/slots-summary")
    public ResponseEntity<?> getSlotSummary() {
        List<Map<String, Object>> data = List.of(
                Map.of("doctorName", "Dr. Ankit", "totalSlots", 30, "bookedSlots", 20, "freeSlots", 10),
                Map.of("doctorName", "Dr. Ramesh", "totalSlots", 25, "bookedSlots", 15, "freeSlots", 10)
        );
        return ResponseEntity.ok(data);
    }

    // ✅ IPD Table
    @GetMapping("/ipd-table")
    public ResponseEntity<?> getIpdTable() {
        List<Map<String, Object>> data = List.of(
                Map.of("patientName", "Ankur Kapoor", "doctorName", "Dr. Ankit", "status", "Admitted", "admitDate", "2025-06-10", "bedNumber", "B12"),
                Map.of("patientName", "Ravi Sharma", "doctorName", "Dr. Ramesh", "status", "Discharged", "admitDate", "2025-05-21", "bedNumber", "C05")
        );
        return ResponseEntity.ok(data);
    }

    // ✅ Helper - Month Name
    private String getMonthName(int month) {
        return switch (month) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "";
        };
    }
}