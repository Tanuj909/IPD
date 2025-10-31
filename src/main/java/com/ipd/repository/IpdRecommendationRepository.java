package com.ipd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.entity.Doctor;
import com.ipd.entity.IpdHospital;
import com.user.entity.Patient;
import com.ipd.entity.IpdRecommendation;
import com.ipd.entity.IpdRecommendationStatus;

public interface IpdRecommendationRepository extends JpaRepository<IpdRecommendation, Long>{
	
	List<IpdRecommendation> findByPatientId(Long patientId);
	
	List<IpdRecommendation> findByDoctorId(Long doctorId);
	
//	Optional<IpdRecommendation> findByAppointment(Appointment appointment);
	
	List<IpdRecommendation> findByHospitalAndStatus(IpdHospital hospital, IpdRecommendationStatus status);

}
