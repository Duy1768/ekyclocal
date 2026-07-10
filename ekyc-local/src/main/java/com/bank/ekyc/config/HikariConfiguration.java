package com.bank.ekyc.config;

import com.bank.ekyc.common.util.AESUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class HikariConfiguration {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String encryptedPassword;

    @Value("${AES_MASTER_KEY}")
    private String masterKey;

    @Value("${spring.datasource.hikari.pool-name}")
    private String poolName;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean
    public DataSource dataSource() {

        String password = AESUtil.decrypt(encryptedPassword, masterKey);

        log.info("============ CUSTOM HIKARI CONFIGURATION ============");
        log.info("Database URL       : {}", url);
        log.info("Database Username  : {}", username);
        log.info("Database Password  : {}", maskPassword(password));
        log.info("Pool Name          : {}", poolName);
        log.info("Minimum Idle       : {}", minimumIdle);
        log.info("Maximum Pool Size  : {}", maximumPoolSize);
        log.info("Idle Timeout       : {} ms", idleTimeout);
        log.info("Max Lifetime       : {} ms", maxLifetime);
        log.info("Connection Timeout : {} ms", connectionTimeout);
        log.info("=====================================================");

        HikariConfig config = new HikariConfig();

        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setPoolName(poolName);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);

        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);

        config.setConnectionTestQuery("SELECT 1");
        config.setAutoCommit(true);

        config.setValidationTimeout(5000);
        config.setInitializationFailTimeout(10000);

        config.setLeakDetectionThreshold(60000);

        config.setRegisterMbeans(true);

        return new HikariDataSource(config);
    }

    private String maskPassword(String password) {

        if (password == null || password.isBlank()) {
            return "********";
        }

        return "*".repeat(password.length());
    }
}