package com.ipd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ipd.entity.IpdModuleSetting;
import com.ipd.service.IpdModuleSettingService;

@RestController
@RequestMapping("/api/ipd/module-setting")
public class IpdModuleSettingController {

    @Autowired
    private IpdModuleSettingService ipdModuleSettingService;

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<IpdModuleSetting> getIpdModuleSettingByAdmin(@PathVariable Long adminId) {
        IpdModuleSetting setting = ipdModuleSettingService.getIpdModuleSettingByAdminId(adminId);
        return ResponseEntity.ok(setting);
    }
}
