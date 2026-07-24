package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.DemoService;
import com.bank.ekyc.infrastructure.external.response.DemoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/demo")
    public DemoResponse demo(
            @RequestParam String responseCode) {

        return demoService.call(responseCode);

    }

}