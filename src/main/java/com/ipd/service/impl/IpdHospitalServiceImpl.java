package com.ipd.service.impl;

import com.ipd.entity.IpdHospital;
import com.ipd.repository.IpdHospitalRepository;
import com.user.entity.User;
import com.user.repository.UserRepository;
import com.ipd.service.IpdHospitalService;
import com.ipd.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IpdHospitalServiceImpl implements IpdHospitalService {

    private final String UPLOAD_DIR = "uploads/hospitals/";

    @Autowired
    private IpdHospitalRepository hospitalRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public IpdHospital createHospital(IpdHospital hospital, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            hospital.setImagePath(saveImage(image));
        }
        return hospitalRepository.save(hospital);
    }

    @Override
    public IpdHospital updateHospital(Long id, IpdHospital updatedHospital, MultipartFile image) throws IOException {
        IpdHospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        hospital.setName(updatedHospital.getName());
        hospital.setAddress(updatedHospital.getAddress());
        hospital.setDescription(updatedHospital.getDescription());

        if (image != null && !image.isEmpty()) {
            hospital.setImagePath(saveImage(image));
        }

        return hospitalRepository.save(hospital);
    }

    @Override
    public void deleteHospital(Long id) {
        IpdHospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        hospitalRepository.delete(hospital);
    }

    @Override
    public List<IpdHospital> getAllHospitals() {
        return hospitalRepository.findAll();
    }

    @Override
    public IpdHospital getHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    @Override
    public IpdHospital mapHospitalToUser(Long userId, Long hospitalId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        IpdHospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
        user.setIpdHospitalId(hospital.getId());
        userRepository.save(user);
        return hospital;
    }

    private String saveImage(MultipartFile image) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String filePath = UPLOAD_DIR + System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path path = Paths.get(filePath);
        Files.write(path, image.getBytes());

        return filePath;
    }
}
