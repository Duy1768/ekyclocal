package com.bank.ekyc.domain.gateway;

import com.bank.ekyc.infrastructure.external.response.DemoResponse;

public interface DemoGateway {

    DemoResponse call(String responseCode);

}