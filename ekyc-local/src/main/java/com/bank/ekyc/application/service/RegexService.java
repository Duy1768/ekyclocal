package com.bank.ekyc.application.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class RegexService {

    private static final Pattern PATTERN =
            Pattern.compile("^(\\d{10}\\s+.+|.+\\s+\\d{10})$");


    public boolean isValid(String content) {

        return PATTERN.matcher(content).matches();

    }

}
