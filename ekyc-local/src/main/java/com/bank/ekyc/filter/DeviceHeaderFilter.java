package com.bank.ekyc.filter;

import com.bank.ekyc.common.dto.DeviceInfo;
import com.bank.ekyc.provider.OshiDeviceInfoProvider;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class DeviceHeaderFilter {

    private static final DeviceInfo DEVICE_INFO =
            new OshiDeviceInfoProvider().getDeviceInfo();

    public static ExchangeFilterFunction deviceHeader() {

        return ExchangeFilterFunction.ofRequestProcessor(request -> {

            ClientRequest newRequest =
                    ClientRequest.from(request)

                            .header("X-Hardware-UUID",
                                    DEVICE_INFO.getHardwareUuid())

                            .header("X-Hostname",
                                    DEVICE_INFO.getHostname())

                            .header("X-Mac-Address",
                                    DEVICE_INFO.getMacAddress())

                            .header("X-OS-Name",
                                    DEVICE_INFO.getOsName())

                            .header("X-OS-Version",
                                    DEVICE_INFO.getOsVersion())

                            .header("X-App-Version",
                                    DEVICE_INFO.getAppVersion())

                            .build();

            return Mono.just(newRequest);

        });

    }

}
