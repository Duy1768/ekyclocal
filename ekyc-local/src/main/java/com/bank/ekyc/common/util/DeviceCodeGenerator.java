package com.bank.ekyc.common.util;

import org.springframework.stereotype.Component;

@Component
public class DeviceCodeGenerator {

    public String generate(Long id) {

        return String.format("CLIENT%06d", id);

    }

}
