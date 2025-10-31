package com.ipd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipd.entity.IpdModuleSetting;
import com.ipd.Exception.ResourceNotFoundException;
import com.ipd.repository.IpdModuleSettingRepository;
import com.ipd.service.IpdModuleSettingService;

@Service
public class IpdModuleSettingServiceImpl implements IpdModuleSettingService {

	@Autowired
	private IpdModuleSettingRepository ipdModuleSettingRepository;

	@Override
	public IpdModuleSetting getIpdModuleSettingByAdminId(Long adminId) {
		return ipdModuleSettingRepository.findByAdminId(adminId).orElseThrow(
				() -> new ResourceNotFoundException("IPD Module Setting not found for Admin ID: " + adminId));
	}
}
