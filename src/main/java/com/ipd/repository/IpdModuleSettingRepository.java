package com.ipd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipd.entity.IpdModuleSetting;

@Repository
public interface IpdModuleSettingRepository extends JpaRepository<IpdModuleSetting, Long> {

	Optional<IpdModuleSetting> findByAdminId(Long adminId);
	
}