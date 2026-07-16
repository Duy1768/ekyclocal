package com.bank.ekyc.presentation.controller;

import com.bank.ekyc.application.service.LunarService;
import com.bank.ekyc.presentation.response.LunarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lunar")
public class LunarController {

    private final LunarService service;

    @GetMapping("/today")
    public LunarResponse today() {

        return service.getToday();

    }

}
