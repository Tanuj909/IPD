package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ipd.Exception.AccessDeniedException;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.service.DoctorService;
import com.user.DTO.DoctorDTO;
import com.user.entity.Admin;
import com.user.entity.Doctor;
import com.user.entity.Patient;
import com.user.entity.User;
import com.user.enums.DoctorStatus;
import com.user.enums.Role;
import com.user.maaper.DoctorMapper;
import com.user.repository.DoctorRepository;
import com.user.repository.PatientRepository;
import com.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;
   
    @Autowired
    private PatientRepository patientRepository;

    private String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

//    private void checkAccess(Doctor doctor) {
//        if (!doctor.getCreatedBy().getUsername().equals(getCurrentUsername()) {
//            throw new AccessDeniedException("Unauthorized access to this doctor");
//        }
//    }
    
    private void checkAccess(Doctor doctor) {
        if (getCurrentUser().getRole()==Role.ADMIN) {
        	if (!doctor.getAdmin().getId().equals(getCurrentUser().getAdmin().getId())) {
        		throw new AccessDeniedException("Unauthorized access to this doctor");
			}
        }else if (getCurrentUser().getRole()==Role.SUPER_ADMIN) {
        	
        		return;
			
        }else if (getCurrentUser().getRole()==Role.DOCTOR) {
        	if (!doctor.getAdmin().getId().equals(getCurrentUser().getDoctor().getAdmin().getId())) {
        		throw new AccessDeniedException("Unauthorized access to this doctor");
			}
        }
        throw new AccessDeniedException("Unauthorized access to this doctor");
    }
    
    @Override
    public List<Doctor> getAllDoctors(String name) {
        User currentUser = getCurrentUser();
        
        Admin admin;
        
        if (currentUser.getRole()==Role.ADMIN) {
			admin = currentUser.getAdmin();
		}else if (currentUser.getRole()==Role.DOCTOR) {
			admin = currentUser.getDoctor().getAdmin();
		}else {
			admin = currentUser.getStaff().getAdmin();
		}
        
        DoctorStatus status = DoctorStatus.APPROVED;

        if (name != null && !name.isEmpty()) {
            return doctorRepository.findByAdminAndStatusAndUser_NameContainingIgnoreCase(admin, status, name);
        }

        return doctorRepository.findByAdminAndStatus(admin, status);
    }


    @Override
    public Doctor getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        checkAccess(doctor);
        return doctor;
    }
    
    @Override
    public Map<String, Long> getDoctorCountBySpecialization() {
        User currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        Admin admin;
        
        if (currentUser.getRole()==Role.ADMIN) {
			admin = currentUser.getAdmin();
		}else if (currentUser.getRole()==Role.DOCTOR) {
			admin = currentUser.getDoctor().getAdmin();
		}else {
			admin = currentUser.getStaff().getAdmin();
		}

        List<Object[]> results = doctorRepository.countBySpecializationGroupByAdmin(admin);

        Map<String, Long> response = new HashMap<>();
        for (Object[] obj : results) {
            response.put((String) obj[0], (Long) obj[1]);
        }
        return response;
    }


//    @Override
//    public List<Doctor> getRecentDoctors(int count) {
//        User currentUser = getCurrentUser();
//        
//        Admin admin;
//        if (currentUser.getRole()==Role.ADMIN) {
//			admin = currentUser.getAdmin();
//		}else if (currentUser.getRole()==Role.DOCTOR) {
//			admin = currentUser.getDoctor().getAdmin();
//		}else {
//			admin = currentUser.getStaff().getAdmin();
//		}
//        
//        return doctorRepository.findRecentDoctorsByAdmin(admin, count);
//    }
    
    @Override
    public List<Doctor> getRecentDoctors(int count) {
        User currentUser = getCurrentUser();
        Admin admin = null;

        if (currentUser.getRole() == Role.ADMIN) {
            admin = currentUser.getAdmin();
        } else if (currentUser.getRole() == Role.DOCTOR) {
            admin = currentUser.getDoctor() != null ? currentUser.getDoctor().getAdmin() : null;
        } else {
            admin = currentUser.getStaff() != null ? currentUser.getStaff().getAdmin() : null;
        }

        if (admin == null) {
            throw new RuntimeException("Admin context not found for current user");
        }

        return doctorRepository.findRecentDoctorsByAdmin(admin, count);
    }


    @Override
    public List<Doctor> getAllDoctorsInHospital() {
    	
    	Admin admin ;
    
        // Check if the authenticated user has the PATIENT role
        boolean isPatient = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_PATIENT"));
        
        if (isPatient) {
            // Look up Patient by email (username) in patients table
        	
        	User user = userRepository.findByEmail(getCurrentUsername()).orElseThrow(()-> new ResourceNotFoundException("Patient Not Found"));
        	
            Patient patient = patientRepository.findByUser(user);
            
            admin = patient.getAdmin();
                  
            if (patient.getAdmin() == null) {
                throw new ResourceNotFoundException("Patient is not mapped to any hospital");
            }
            admin = patient.getAdmin();
        } else {
            // Look up User in users table
            User currentUser = getCurrentUser();
            
            if (currentUser.getRole()==Role.ADMIN) {
    			admin = currentUser.getAdmin();
    		}else if (currentUser.getRole()==Role.DOCTOR) {
    			admin = currentUser.getDoctor().getAdmin();
    		}else {
    			admin = currentUser.getStaff().getAdmin();
    		}
//            hospital = currentUser.getHospital();
        }

        return doctorRepository.findByAdminAndStatus(admin, DoctorStatus.APPROVED);
    }

    @Override
    public List<Doctor> getAllDoctors(String name, String specialization) {
    	Admin admin ;
        
        // Check if the authenticated user has the PATIENT role
        boolean isPatient = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_PATIENT"));
        
        if (isPatient) {
            // Look up Patient by email (username) in patients table
        	
        	User user = userRepository.findByEmail(getCurrentUsername()).orElseThrow(()-> new ResourceNotFoundException("Patient Not Found"));
        	
            Patient patient = patientRepository.findByUser(user);
            
            admin = patient.getAdmin();
                  
            if (patient.getAdmin() == null) {
                throw new ResourceNotFoundException("Patient is not mapped to any hospital");
            }
            admin = patient.getAdmin();
        } else {
            // Look up User in users table
            User currentUser = getCurrentUser();
            
            if (currentUser.getRole()==Role.ADMIN) {
    			admin = currentUser.getAdmin();
    		}else if (currentUser.getRole()==Role.DOCTOR) {
    			admin = currentUser.getDoctor().getAdmin();
    		}else {
    			admin = currentUser.getStaff().getAdmin();
    		}
//            hospital = currentUser.getHospital();
        }

        DoctorStatus status = DoctorStatus.APPROVED;

        if (name != null && !name.isEmpty() && specialization != null && !specialization.isEmpty()) {
            return doctorRepository.findByAdminAndStatusAndUser_NameContainingIgnoreCaseAndSpecializationContainingIgnoreCase(
                    admin, status, name, specialization);
        }

        if (name != null && !name.isEmpty()) {
            return doctorRepository.findByAdminAndStatusAndUser_NameContainingIgnoreCase(admin, status, name);
        }

        if (specialization != null && !specialization.isEmpty()) {
            return doctorRepository.findByAdminAndStatusAndSpecializationContainingIgnoreCase(admin, status, specialization);
        }

        return doctorRepository.findByAdminAndStatus(admin, status);
    }
    
	@Override
	public DoctorDTO getDoctorByEmail(String Email) {
		User userDoctor = userRepository.findByEmail(Email)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: " + Email));
		
		Doctor doctor = userDoctor.getDoctor();
		
        DoctorDTO dto = DoctorMapper.toDTO(doctor);
		
		return dto;
	}

}