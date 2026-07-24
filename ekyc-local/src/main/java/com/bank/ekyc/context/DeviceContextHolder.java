package com.bank.ekyc.context;

import com.bank.ekyc.domain.entity.Device;

public final class DeviceContextHolder {

    private static final ThreadLocal<Device> DEVICE_CONTEXT =
            new ThreadLocal<>();

    private DeviceContextHolder() {
    }

    public static void setCurrentDevice(Device device) {
        DEVICE_CONTEXT.set(device);
    }

    public static Device getCurrentDevice() {
        return DEVICE_CONTEXT.get();
    }

    public static void clear() {
        DEVICE_CONTEXT.remove();
    }

}
