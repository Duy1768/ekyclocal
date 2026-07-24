package com.bank.ekyc.application.service;

import com.bank.ekyc.common.util.DeviceCodeGenerator;
import com.bank.ekyc.domain.entity.Device;
import com.bank.ekyc.infrastructure.dao.DeviceDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceDAO deviceDAO;

    private final DeviceCodeGenerator generator;

    public Device saveOrUpdate(Device device) {

        Device exist = deviceDAO.findByHardwareUuid(device.getHardwareUuid());

        LocalDateTime now = LocalDateTime.now();

        if (exist == null) {

            device.setCreatedDate(now);
            device.setUpdatedDate(now);
            device.setLastSeen(now);

            Long id = deviceDAO.insert(device);

            String deviceCode = generator.generate(id);

            deviceDAO.updateDeviceCode(id, deviceCode);

            device.setId(id);
            device.setDeviceCode(deviceCode);

            return device;
        }

        exist.setHostname(device.getHostname());
        exist.setMacAddress(device.getMacAddress());
        exist.setIpAddress(device.getIpAddress());
        exist.setOsName(device.getOsName());
        exist.setOsVersion(device.getOsVersion());
        exist.setAppVersion(device.getAppVersion());
        exist.setUpdatedDate(now);
        exist.setLastSeen(now);

        deviceDAO.update(exist);

        return exist;
    }

    public Device findByHardwareUuid(String hardwareUuid) {
        return deviceDAO.findByHardwareUuid(hardwareUuid);
    }

    public List<Device> findAll() {
        return deviceDAO.findAll();
    }
}