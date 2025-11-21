package com.ipd.scheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdAutoDischargeRecord;
import com.ipd.entity.IpdDischargeSummary;
import com.ipd.entity.IpdTreatmentUpdate;
import com.ipd.entity.IpdVital;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdAutoDischargeRecordRepository;
import com.ipd.service.IpdVitalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IpdAutoDischargeSchedulerServiceImpl implements IpdAutoDischargeSchedulerService {

    private final IpdAdmissionRepository admissionRepo;
    private final IpdAutoDischargeRecordRepository autoRecordRepo;
    private final IpdVitalService vitalService;

    @Override
//    @Scheduled(cron = "0 0 12,18 * * ?")
    @Scheduled(cron = "30 * * * * ?")
    @Transactional
    public void runAutoDischarge() {

        // Fetch all admissions where outcome created and not yet discharged
        List<IpdAdmission> admissions = admissionRepo.findByIsOutcomeCreatedTrueAndIsDischargedFalse();

        for (IpdAdmission admission : admissions) {

            // ❗ Discharge Admission
            admission.setDischarged(true);
            admission.setDischargeDate(LocalDateTime.now());
            admissionRepo.save(admission);

            // ❗ Create Auto Discharge Record
            IpdAutoDischargeRecord record = new IpdAutoDischargeRecord();

            record.setAdmission(admission);
            record.setHospital(admission.getHospital());
            record.setAutoDischarged(true);
            record.setDischargedAt(LocalDateTime.now());

            // Map Treatment Summary
//            List<IpdTreatmentUpdate> treatments = admission.getTreatmentUpdates();
//            record.setTreatmentSummary(treatments);
            
            List<IpdTreatmentUpdate> treatments = new ArrayList<>(admission.getTreatmentUpdates());
            record.setTreatmentSummary(treatments);


            // Map Admission Vitals
            List<IpdVital> vitals = vitalService.getAdmissionVitals(admission.getId());
            record.setAdmissionVitals(vitals);

            // Map Discharge Summary (If created by doctor before)
            IpdDischargeSummary dischargeSummary = admission.getDischargeSummary();
            record.setDischargeSummary(dischargeSummary);

            autoRecordRepo.save(record);
        }
    }
}
