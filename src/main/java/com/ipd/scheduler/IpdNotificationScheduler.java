package com.ipd.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ipd.entity.IpdAdmission;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdTreatmentUpdateRepository;
import com.ipd.repository.IpdVitalRepository;
import com.user.entity.Admin;
import com.user.entity.User;
import com.user.enums.Role;
import com.user.repository.UserRepository;
import com.user.service.NotificationService;

@Component
public class IpdNotificationScheduler {

    private final IpdAdmissionRepository admissionRepo;
    private final IpdTreatmentUpdateRepository treatmentRepo;
    private final IpdVitalRepository vitalRepo;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public IpdNotificationScheduler(
            IpdAdmissionRepository admissionRepo,
            IpdTreatmentUpdateRepository treatmentRepo,
            IpdVitalRepository vitalRepo,
            NotificationService notificationService,
            UserRepository userRepository) {

        this.admissionRepo = admissionRepo;
        this.treatmentRepo = treatmentRepo;
        this.vitalRepo = vitalRepo;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }
    
    private Admin getAdmin(User user) {
  	   
  	   Admin admin;
  	   
  		if (user.getRole() == Role.ADMIN) {
  			admin = user.getAdmin();
  		} else if (user.getRole() == Role.DOCTOR) {
  			admin = user.getDoctor().getAdmin();
  		} else {
  			admin = user.getStaff().getAdmin();
  		}
  		
  		return admin;
     }
     

    @Scheduled(cron = "0 0 11 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void sendDailyIpdReminders() {

    	 System.out.println("⏰ IPD Scheduler Triggered at " + LocalDateTime.now());
    	
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        List<IpdAdmission> activeAdmissions =
                admissionRepo.findByIsDischargedFalse();


        for (IpdAdmission admission : activeAdmissions) {

            Long createdByUserId = admission.getCreatedBy();

            Optional<User> userOpt = userRepository.findById(createdByUserId);

            if (userOpt.isEmpty()) {
                System.out.println(
                        "⚠ User not found for Admission ID "
                                + admission.getId()
                                + ", User ID: "
                                + createdByUserId
                );
                continue;
            }

            User user = userOpt.get();

            Admin admin = getAdmin(user);

            if (admin == null) {
                System.out.println(
                        "⚠ Admin not mapped for User ID "
                                + user.getId()
                                + " (Admission ID: "
                                + admission.getId() + ")"
                );
                continue;
            }

            Long adminId = admin.getId();

            // 1️⃣ Treatment check
            boolean treatmentUpdated =
                    treatmentRepo.existsByAdmissionIdAndCreatedAtBetween(
                            admission.getId(), startOfDay, endOfDay);

            if (!treatmentUpdated) {
                notificationService.notifyByRole(
                        adminId,
                        Role.DOCTOR,
                        "Treatment Update Pending",
                        "Today's treatment is not updated for Admission ID: "
                                + admission.getId()
                );
            }

            // 2️⃣ Vitals check
            boolean vitalsUpdated =
                    vitalRepo.existsByAdmissionIdAndCreatedAtBetween(
                            admission.getId(), startOfDay, endOfDay);

            if (!vitalsUpdated) {

                notificationService.notifyByRole(
                        adminId,
                        Role.NURSE,
                        "Vitals Update Pending",
                        "Vitals not updated today for Admission ID: "
                                + admission.getId()
                );

                notificationService.notifyByRole(
                        adminId,
                        Role.DOCTOR,
                        "Vitals Update Pending",
                        "Vitals not updated today for Admission ID: "
                                + admission.getId()
                );
            }
        }
    }
}
