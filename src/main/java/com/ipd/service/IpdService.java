package com.ipd.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ipd.billing.dto.SpecialDiscountRequestDTO;
import com.ipd.billing.dto.SpecialDiscountResponseDTO;
import com.ipd.billing.dto.UpdateIsDailyRequest;
import com.ipd.dto.AdmissionChartPoint;
import com.ipd.dto.IpdAdmissionUpdateRequest;
import com.ipd.dto.IpdDashboardSummary;
import com.ipd.dto.IpdPaymentHistoryResponseDTO;
import com.ipd.dto.IpdPaymentRequestDTO;
import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdBilling;
import com.ipd.entity.IpdRoom;

public interface IpdService {

    IpdAdmission admitPatient(Long patientId, Long doctorId, Long ipdBedId, Long roomId, String reason,Double advanceAmount,                    // ← Optional
            String advancePaymentMode);

    IpdAdmission generateBilling(Long admissionId,Double advanceAmount,String advancePaymentMode);
    
    void regenerateBill(Long admissionId);
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
	
//	IpdAdmission admitFromRecommendation(Long recommendationId, Long roomId,Long bedId, String reason);
	
	String processPayment(IpdPaymentRequestDTO request);

	Long findFirstAvailableRoomId();

	List<IpdAdmission> getAllDischargeAdmissionsForHospital();

	IpdAdmission updateAdmissionFully(Long id, IpdAdmissionUpdateRequest request);

	List<IpdPaymentHistoryResponseDTO> getPaymentHistory(Long admissionId);

	SpecialDiscountResponseDTO specialDiscounts(SpecialDiscountRequestDTO request);

	String changeServiceDailyStatus(UpdateIsDailyRequest request);

//	String closeBillOnDischarge(Long admissionId);

    
}