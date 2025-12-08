package com.ipd.service;

import com.ipd.entity.IpdLabTest;

import java.util.List;

public interface IpdLabTestService {

    IpdLabTest createLabReport(Long admissionId, IpdLabTest labTest);

    List<IpdLabTest> getAllLabReportsByAdmission(Long admissionId);

    IpdLabTest getLabReportById(Long labTestId);

    IpdLabTest updateLabReport(Long labTestId, IpdLabTest updatedData);

    void deleteLabReport(Long labTestId);
}
