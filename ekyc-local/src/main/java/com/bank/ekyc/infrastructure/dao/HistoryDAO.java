package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.infrastructure.external.response.HistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public List<HistoryResponse> findAll() {

        String sql = """
                SELECT
                    d.device_code,
                    d.hostname,
                    d.os_name,
                    d.mac_address,
                    h.api_name,
                    h.status,
                    h.response_code,
                    h.duration_ms,
                    h.request_time
                FROM third_party_call_history h
                JOIN device d
                    ON h.device_id = d.id
                ORDER BY h.request_time DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                HistoryResponse.builder()
                        .deviceCode(rs.getString("device_code"))
                        .hostname(rs.getString("hostname"))
                        .osName(rs.getString("os_name"))
                        .macAddress(rs.getString("mac_address"))
                        .apiName(rs.getString("api_name"))
                        .status(rs.getString("status"))
                        .responseCode(rs.getString("response_code"))
                        .durationMs(rs.getLong("duration_ms"))
                        .requestTime(rs.getTimestamp("request_time").toLocalDateTime())
                        .build()
        );
    }
}