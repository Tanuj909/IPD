package com.ipd.service;

import com.ipd.dto.transfer.IcuToIpdRequest;
import com.ipd.dto.transfer.IpdToIcuRequest;
import com.ipd.entity.IpdAdmission;

public interface IpdIcuTransferService {
	
	IpdAdmission receiveFromIcu(IcuToIpdRequest request);

	void transferToIcu(IpdToIcuRequest request);
	
}