//package com.ipd.service.impl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.ipd.Exception.ResourceNotFoundException;
//import com.ipd.entity.IpdDoctorVisit;
//import com.ipd.repository.DoctorVisitRepository;
//import com.ipd.service.DoctorVisitService;
//import com.user.entity.Doctor;
//import com.user.repository.DoctorRepository;
//
//@Service
//public class DoctorVisitServiceImpl implements DoctorVisitService {
//
//    @Autowired
//    private DoctorVisitRepository doctorVisitRepo;
//    
//    @Autowired
//    private DoctorRepository doctorRepo;
//     
//    @Override
//    public IpdDoctorVisit updateVisitCount(Long visitId, Integer visitCount) {
//        if (visitCount == null || visitCount < 1) {
//            throw new IllegalArgumentException("Visit count must be at least 1");
//        }
//
//        IpdDoctorVisit visit = doctorVisitRepo.findById(visitId)
//                .orElseThrow(() -> new ResourceNotFoundException("Doctor visit not found"));
//        
//        Doctor doctor = doctorRepo.findById(visit.getDoctorId())
//                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
//        
//        double perVisitFee = (visit.getFee() != null) ? visit.getFee() : doctor.getConsultationFee();
//
//        visit.setVisitCount(visitCount);
//        visit.setTotalDoctorFees(perVisitFee * visitCount);   // NEW LOGIC
//        
//        return doctorVisitRepo.save(visit);
//    }
//
//    @Override
//    public List<IpdDoctorVisit> getVisitsByAdmission(Long admissionId) {
//        return doctorVisitRepo.findByAdmissionId(admissionId);
//    }
//
////    @Override
////    public double calculateTotalDoctorFees(Long admissionId) {
////        List<IpdDoctorVisit> visits = doctorVisitRepo.findByAdmissionId(admissionId);
////
////        return visits.stream()
////                .mapToDouble(visit -> {
////                    Doctor doctor = doctorRepo.findById(visit.getDoctorId())
////                            .orElseThrow(() ->
////                                    new ResourceNotFoundException("Doctor not found with ID: " + visit.getDoctorId()));
////
////                    double perVisitFee = (visit.getFee() != null) ? visit.getFee() : doctor.getConsultationFee(); // Use custom fee if set, else default
////                    return visit.getVisitCount() * perVisitFee;
////                })
////                .sum();
////    }
//    
//    @Override
//    public double calculateTotalDoctorFees(Long admissionId) {
//        return doctorVisitRepo.findByAdmissionId(admissionId)
//                .stream()
//                .mapToDouble(v -> v.getTotalDoctorFees() != null ? v.getTotalDoctorFees() : 0.0)
//                .sum();
//    }
//
//}

package com.ipd.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.entity.IpdDoctorVisit;
import com.ipd.enums.IsDaily;
import com.ipd.repository.DoctorVisitRepository;
import com.ipd.service.DoctorVisitService;
import com.user.entity.Doctor;
import com.user.repository.DoctorRepository;

@Service
public class DoctorVisitServiceImpl implements DoctorVisitService {

    @Autowired
    private DoctorVisitRepository doctorVisitRepo;
   
    @Autowired
    private DoctorRepository doctorRepo;
    
    @Override
    public IpdDoctorVisit updateVisitCount(Long visitId, Integer visitCount) {
        if (visitCount == null || visitCount < 1) {
            throw new IllegalArgumentException("Visit count must be at least 1");
        }
        IpdDoctorVisit visit = doctorVisitRepo.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor visit not found"));
       
        Doctor doctor = doctorRepo.findById(visit.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
       
        double perVisitFee = (visit.getFee() != null) ? visit.getFee() : doctor.getConsultationFee();
        visit.setVisitCount(visitCount);
        visit.setTotalDoctorFees(perVisitFee * visitCount); // For non-daily, this is fine
       
        return doctorVisitRepo.save(visit);
    }

    @Override
    public List<IpdDoctorVisit> getVisitsByAdmission(Long admissionId) {
        return doctorVisitRepo.findByAdmissionId(admissionId);
    }

    /**
     * Calculates the total doctor fees for an admission.
     * 
     * For visits where isDaily = NO:
     *   - Uses the pre-stored totalDoctorFees (based on visitCount * perVisitFee)
     * 
     * For visits where isDaily = YES:
     *   - Dynamically calculates the number of days from visitDate to today (inclusive)
     *   - Multiplies by the per-visit (daily) fee
     *   - This allows automatic accrual of daily fees without manual updates
     */
    @Override
    @Transactional  // Removed readOnly = true → now allows writes
    public double calculateTotalDoctorFees(Long admissionId) {
        LocalDate today = LocalDate.now();

        List<IpdDoctorVisit> visits = doctorVisitRepo.findByAdmissionId(admissionId);

        double grandTotal = 0.0;

        for (IpdDoctorVisit v : visits) {
            Doctor doctor = doctorRepo.findById(v.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + v.getDoctorId()));

            double perVisitFee = (v.getFee() != null) ? v.getFee() : doctor.getConsultationFee();

            if (v.getIsDaily() == IsDaily.YES) {
                // Dynamic daily calculation
                LocalDate visitLocalDate = v.getVisitDate().toLocalDate();
                long daysInclusive = ChronoUnit.DAYS.between(visitLocalDate, today) + 1;
                if (daysInclusive < 1) daysInclusive = 1;

                int effectiveVisitCount = (int) daysInclusive;
                double dailyTotal = perVisitFee * effectiveVisitCount;

                // UPDATE the entity fields
                v.setVisitCount(effectiveVisitCount);
                v.setTotalDoctorFees(dailyTotal);

                // Since we're in a loop and entity is managed (fetched via repo), save will persist changes
                doctorVisitRepo.save(v);

                grandTotal += dailyTotal;

            } else {
                // For non-daily: use stored values, fallback to recalculate if missing/corrupt
                double storedTotal = (v.getTotalDoctorFees() != null) ? v.getTotalDoctorFees() : 0.0;
                int count = (v.getVisitCount() != null) ? v.getVisitCount() : 1;

                if (storedTotal == 0.0) {
                    storedTotal = perVisitFee * count;
                    v.setTotalDoctorFees(storedTotal);
                    doctorVisitRepo.save(v); // Ensure consistency
                }

                grandTotal += storedTotal;
            }
        }

        return grandTotal;
    }
}