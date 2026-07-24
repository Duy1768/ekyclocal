package com.bank.ekyc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "third-party.regex")
public class ThirdPartyProperties {

    private String baseUrl;

    private String apiKey;

    private Retry retry = new Retry();

    private Timeout timeout = new Timeout();

    private Pool pool = new Pool();

    @Data
    public static class Retry {

        private Integer maxAttempts;

        private Integer delay;

    }

    @Data
    public static class Timeout {

        private Integer connect;

        private Integer response;

        private Integer read;

        private Integer write;

        private Integer overall;

    }

    @Data
    public static class Pool {

        private String name;

        private Integer maxConnections;

        private Integer pendingAcquireTimeout;

    }

}
