package com.ipd.service;

import com.ipd.entity.IpdHospital;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IpdHospitalService {
	
	IpdHospital createHospital(IpdHospital hospital, MultipartFile image) throws IOException;

	IpdHospital updateHospital(Long id, IpdHospital hospital, MultipartFile image) throws IOException;

	void deleteHospital(Long id);

	List<IpdHospital> getAllHospitals();

	IpdHospital getHospitalById(Long id);

	IpdHospital mapHospitalToUser(Long userId, Long hospitalId);
	
}
