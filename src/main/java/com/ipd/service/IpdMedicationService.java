package com.ipd.service;

import java.util.List;
import com.ipd.entity.IpdMedication;

public interface IpdMedicationService {

    IpdMedication createMedication(IpdMedication medication, Long admissionId, Long treatmentUpdateId);

    List<IpdMedication> getMedicationsByAdmission(Long admissionId);

    List<IpdMedication> getMedicationsByTreatmentUpdate(Long treatmentUpdateId);

    IpdMedication updateMedication(Long medicationId, IpdMedication updatedMedication);

    void deleteMedication(Long medicationId);
}
