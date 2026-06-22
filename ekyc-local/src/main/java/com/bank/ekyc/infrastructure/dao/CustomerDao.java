package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public int insert(Customer customer) {

        log.info("step=database_insert_started entity=customer customerCode={}", customer.getCustomerCode());

        String sql = """
                INSERT INTO customer
                (
                    customer_code,
                    full_name,
                    id_number,
                    phone,
                    email,
                    created_time
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?
                )
                """;

        int insertedRows =
                jdbcTemplate.update(
                sql,
                customer.getCustomerCode(),
                customer.getFullName(),
                customer.getIdNumber(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getCreatedTime()
        );

        log.info(
                "step=database_insert_completed entity=customer customerCode={} affectedRows={}",
                customer.getCustomerCode(),
                insertedRows);

        return insertedRows;
    }
}
