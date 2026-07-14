package com.bank.ekyc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "third-party.lunar")
public class LunarProperties {

    private String baseUrl;

    private String apiKey;

    private Timeout timeout;

    private Pool pool;

    @Data
    public static class Timeout {

        private Integer connect;
        private Integer response;
        private Integer read;
        private Integer write;

    }

    @Data
    public static class Pool {

        private String name;

        private Integer maxConnections;

        private Integer pendingAcquireTimeout;

    }

}