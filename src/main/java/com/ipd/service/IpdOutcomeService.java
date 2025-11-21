package com.ipd.service;

import com.ipd.entity.IpdOutcome;

public interface IpdOutcomeService {

    IpdOutcome createOutcome(Long admissionId, IpdOutcome req);

    IpdOutcome updateOutcome(Long outcomeId, IpdOutcome req);

    void deleteOutcome(Long outcomeId);

    IpdOutcome getOutcome(Long admissionId);
}