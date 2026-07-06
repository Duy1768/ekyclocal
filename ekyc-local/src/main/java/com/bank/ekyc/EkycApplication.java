package com.bank.ekyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class EkycApplication {

    public static void main(String[] args) {
        SpringApplication.run(EkycApplication.class, args);
    }

}