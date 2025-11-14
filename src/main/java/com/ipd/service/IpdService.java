package com.ipd.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ipd.dto.AdmissionChartPoint;
import com.ipd.dto.IpdDashboardSummary;
import com.ipd.dto.IpdPaymentRequestDTO;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdBilling;
import com.ipd.entity.IpdRoom;

public interface IpdService {

    IpdAdmission admitPatient(Long patientId, Long doctorId, Long roomId, String reason);

    IpdAdmission generateBilling(Long admissionId);
//    String generateBill(Long admissionId);
    
    void dischargeAfterPayment(Long admissionId);

    IpdBilling getBillingByAdmission(Long admissionId);

    List<IpdAdmission> getAllAdmissionsForHospital();

    List<IpdRoom> getAvailableRooms();

    IpdRoom createRoom(IpdRoom room);

    IpdRoom updateRoom(Long roomId, IpdRoom updatedRoom);

    void deleteRoom(Long roomId);

	IpdRoom getRoomByRoomNumber(String roomNumber);

	List<AdmissionChartPoint> getAdmissionChart(LocalDateTime from, LocalDateTime to);

	IpdDashboardSummary getDashboardSummary();

	IpdBilling makePartialOrFullPayment(Long admissionId, double amount);

	IpdBilling addAdditionalDoctorFee(Long admissionId, double fee);

	List<IpdAdmission> getAllAdmissionsForCurrentHospital();

	List<IpdBilling> getAllBillingForCurrentHospital();

	IpdRoom getRoomById(Long roomId);

	boolean isIpdEnabledForCurrentHospital();
	
	IpdAdmission admitFromRecommendation(Long recommendationId, Long roomId, String reason);
	
	String processPayment(IpdPaymentRequestDTO request);

//	DoctorVisit addDoctorVisit(Long admissionId, Long doctorId);

	
	
	
//	Optional<IpdAdmission> findByPatientIdAndIsDischargedFalse(Long patientId);

    
}