package com.bank.ekyc.application.service;

import com.bank.ekyc.domain.gateway.DemoGateway;
import com.bank.ekyc.infrastructure.external.response.DemoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoService {

    private final DemoGateway demoGateway;

    public DemoResponse call(String responseCode) {

        return demoGateway.call(responseCode);

    }

}