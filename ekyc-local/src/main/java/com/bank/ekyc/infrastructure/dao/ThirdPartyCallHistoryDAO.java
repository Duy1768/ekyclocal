package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.domain.entity.ThirdPartyCallHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ThirdPartyCallHistoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public void insert(ThirdPartyCallHistory history) {

        String sql = """
                INSERT INTO third_party_call_history
                (
                    device_id,
                    api_name,
                    transaction_time,
                    request_time,
                    response_time,
                    duration_ms,
                    response_code,
                    status,
                    error_message
                )
                VALUES
                (
                    ?,?,?,?,?,?,?,?,?
                )
                """;

        jdbcTemplate.update(
                sql,
                history.getDeviceId(),
                history.getApiName(),
                Timestamp.valueOf(history.getTransactionTime()),
                Timestamp.valueOf(history.getRequestTime()),
                Timestamp.valueOf(history.getResponseTime()),
                history.getDurationMs(),
                history.getResponseCode(),
                history.getStatus(),
                history.getErrorMessage()
        );
    }



}