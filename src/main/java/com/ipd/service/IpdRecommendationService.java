package com.ipd.service;

import com.ipd.entity.IpdHospital;
import com.ipd.dto.IpdRecommendationCreateDTO;
import com.ipd.dto.IpdRecommendationResponseDTO;
import com.ipd.entity.IpdAdmission;

import java.util.List;

public interface IpdRecommendationService {
    IpdRecommendationResponseDTO createRecommendation(IpdRecommendationCreateDTO dto);
    List<IpdRecommendationResponseDTO> getRecommendationsByPatient(String email);
    List<IpdRecommendationResponseDTO> getRecommendationsByDoctor(String email);
    IpdAdmission convertToAdmission(Long recommendationId, Long roomId, Long bedId,Double advanceAmount, String advancePaymentMode);
    List<IpdRecommendationResponseDTO> getPendingRecommendationsByHospital(IpdHospital hospital);
}