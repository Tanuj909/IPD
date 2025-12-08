package com.ipd.service.impl;

import com.ipd.entity.IpdAdmission;
import com.ipd.entity.IpdLabTest;
import com.ipd.repository.IpdAdmissionRepository;
import com.ipd.repository.IpdLabTestRepository;
import com.ipd.service.IpdLabTestService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IpdLabTestServiceImpl implements IpdLabTestService {

    private final IpdLabTestRepository labTestRepository;
    private final IpdAdmissionRepository admissionRepository;

    @Override
    public IpdLabTest createLabReport(Long admissionId, IpdLabTest labTest) {

        IpdAdmission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        labTest.setAdmission(admission);
        labTest.setCreatedAt(LocalDateTime.now());
//        labTest.setUpdatedAt(LocalDateTime.now());

        return labTestRepository.save(labTest);
    }

    @Override
    public List<IpdLabTest> getAllLabReportsByAdmission(Long admissionId) {
        return labTestRepository.findByAdmissionId(admissionId);
    }

    @Override
    public IpdLabTest getLabReportById(Long labTestId) {
        return labTestRepository.findById(labTestId)
                .orElseThrow(() -> new RuntimeException("Lab report not found"));
    }

    @Override
    public IpdLabTest updateLabReport(Long labTestId, IpdLabTest updatedData) {

        IpdLabTest existing = labTestRepository.findById(labTestId)
                .orElseThrow(() -> new RuntimeException("Lab report not found"));

        existing.setTestName(updatedData.getTestName());
        existing.setTestResults(updatedData.getTestResults());
        existing.setNotes(updatedData.getNotes());
        existing.setReportUrl(updatedData.getReportUrl());
        existing.setUpdatedAt(LocalDateTime.now());

        return labTestRepository.save(existing);
    }

    @Override
    public void deleteLabReport(Long labTestId) {
        IpdLabTest lab = labTestRepository.findById(labTestId)
                .orElseThrow(() -> new RuntimeException("Lab report not found"));

        labTestRepository.delete(lab);
    }
}
