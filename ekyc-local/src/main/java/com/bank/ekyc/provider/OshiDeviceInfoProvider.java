package com.bank.ekyc.provider;

import com.bank.ekyc.common.dto.DeviceInfo;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.net.InetAddress;
import java.util.List;

public class OshiDeviceInfoProvider {

    public DeviceInfo getDeviceInfo() {

        SystemInfo systemInfo = new SystemInfo();

        HardwareAbstractionLayer hardware =
                systemInfo.getHardware();

        OperatingSystem os =
                systemInfo.getOperatingSystem();

        ComputerSystem computerSystem =
                hardware.getComputerSystem();

        String hardwareUuid =
                computerSystem.getHardwareUUID();

        String hostname = getHostName();

        String macAddress = getMacAddress(hardware.getNetworkIFs());

        String osName =
                os.getFamily();

        String osVersion =
                os.getVersionInfo().getVersion();

        return DeviceInfo.builder()
                .hardwareUuid(hardwareUuid)
                .hostname(hostname)
                .macAddress(macAddress)
                .osName(osName)
                .osVersion(osVersion)
                .appVersion("1.0.0")
                .build();
    }

    private String getHostName() {

        try {

            return InetAddress.getLocalHost().getHostName();

        } catch (Exception e) {

            return "UNKNOWN";
        }
    }

    private String getMacAddress(List<NetworkIF> networkIFS) {

        for (NetworkIF networkIF : networkIFS) {

            networkIF.updateAttributes();

            String mac = networkIF.getMacaddr();

            if (mac != null && !mac.isBlank()) {

                return mac;
            }
        }

        return "";
    }

}