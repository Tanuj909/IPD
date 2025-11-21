package com.ipd.service;

import java.util.List;

import com.ipd.entity.IpdDischargeSummary;

public interface IpdDischargeSummaryService {

    IpdDischargeSummary create(Long admissionId, IpdDischargeSummary request);

    IpdDischargeSummary update(Long summaryId, IpdDischargeSummary request);

    void delete(Long summaryId);

    IpdDischargeSummary getByAdmission(Long admissionId);

    List<IpdDischargeSummary> getAllByHospital();

}
