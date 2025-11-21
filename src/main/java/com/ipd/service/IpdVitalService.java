package com.ipd.service;

import java.util.List;
import com.ipd.entity.IpdVital;
import com.ipd.enums.VitalType;

public interface IpdVitalService {

    IpdVital createVital(Long admissionId, IpdVital vital);

    IpdVital updateVital(Long vitalId, IpdVital updatedVital);

    void deleteVital(Long vitalId);

    List<IpdVital> getAdmissionVitals(Long admissionId);

    List<IpdVital> getVitalsByType(Long admissionId, VitalType type);
}
