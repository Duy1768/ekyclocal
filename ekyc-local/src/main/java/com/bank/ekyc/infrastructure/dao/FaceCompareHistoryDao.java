package com.bank.ekyc.infrastructure.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class FaceCompareHistoryDao {

    private final JdbcTemplate jdbcTemplate;

    public int insert(
            String customerCode,
            String selfieImagePath,
            String selfieChecksum,
            Double similarity,
            String compareStatus) {

        String sql = """
                INSERT INTO face_compare_history
                (
                    customer_code,
                    selfie_image_path,
                    selfie_checksum,
                    similarity,
                    compare_status,
                    created_time
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?
                )
                """;

        return jdbcTemplate.update(
                sql,
                customerCode,
                selfieImagePath,
                selfieChecksum,
                similarity,
                compareStatus,
                LocalDateTime.now()
        );
    }
}