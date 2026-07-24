package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.DeviceService;
import com.bank.ekyc.domain.entity.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public List<Device> findAll() {
        return deviceService.findAll();
    }

    @GetMapping("/{hardwareUuid}")
    public Device findByHardwareUuid(
            @PathVariable String hardwareUuid) {

        return deviceService.findByHardwareUuid(hardwareUuid);
    }

}