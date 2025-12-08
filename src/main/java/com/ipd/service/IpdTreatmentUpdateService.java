package com.ipd.service;

import com.ipd.entity.IpdTreatmentUpdate;

import java.util.List;

public interface IpdTreatmentUpdateService {

    IpdTreatmentUpdate createTreatment(Long AdmissonId, IpdTreatmentUpdate req);

    IpdTreatmentUpdate updateTreatment(Long id, IpdTreatmentUpdate req);

    void deleteTreatment(Long id);

    List<IpdTreatmentUpdate> getAllTreatments(Long admissionId);

    IpdTreatmentUpdate getLatestTreatment(Long admissionId);

}
