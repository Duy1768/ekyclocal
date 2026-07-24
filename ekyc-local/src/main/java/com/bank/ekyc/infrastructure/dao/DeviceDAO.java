package com.bank.ekyc.infrastructure.dao;

import com.bank.ekyc.domain.entity.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeviceDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Device> ROW_MAPPER = (rs, rowNum) ->
            Device.builder()
                    .id(rs.getLong("id"))
                    .deviceCode(rs.getString("device_code"))
                    .hardwareUuid(rs.getString("hardware_uuid"))
                    .hostname(rs.getString("hostname"))
                    .macAddress(rs.getString("mac_address"))
                    .ipAddress(rs.getString("ip_address"))
                    .osName(rs.getString("os_name"))
                    .osVersion(rs.getString("os_version"))
                    .appVersion(rs.getString("app_version"))
                    .createdDate(toLocalDateTime(rs.getTimestamp("created_date")))
                    .updatedDate(toLocalDateTime(rs.getTimestamp("updated_date")))
                    .lastSeen(toLocalDateTime(rs.getTimestamp("last_seen")))
                    .build();

    public Device findByHardwareUuid(String hardwareUuid) {

        String sql = """
                SELECT *
                FROM device
                WHERE hardware_uuid = ?
                """;

        List<Device> list = jdbcTemplate.query(sql, ROW_MAPPER, hardwareUuid);

        return list.isEmpty() ? null : list.get(0);
    }

    public Device findById(Long id) {

        String sql = """
                SELECT *
                FROM device
                WHERE id = ?
                """;

        List<Device> list = jdbcTemplate.query(sql, ROW_MAPPER, id);

        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Insert và trả về id vừa sinh bởi BIGSERIAL
     */
    public Long insert(Device device) {

        String sql = """
                INSERT INTO device
                (
                    hardware_uuid,
                    hostname,
                    mac_address,
                    ip_address,
                    os_name,
                    os_version,
                    app_version,
                    created_date,
                    updated_date,
                    last_seen
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                RETURNING id
                """;

        return jdbcTemplate.queryForObject(
                sql,
                Long.class,
                device.getHardwareUuid(),
                device.getHostname(),
                device.getMacAddress(),
                device.getIpAddress(),
                device.getOsName(),
                device.getOsVersion(),
                device.getAppVersion(),
                Timestamp.valueOf(device.getCreatedDate()),
                Timestamp.valueOf(device.getUpdatedDate()),
                Timestamp.valueOf(device.getLastSeen())
        );
    }

    public void updateDeviceCode(Long id, String deviceCode) {

        String sql = """
                UPDATE device
                SET device_code = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql, deviceCode, id);
    }

    public void update(Device device) {

        String sql = """
                UPDATE device
                SET
                    hostname = ?,
                    mac_address = ?,
                    ip_address = ?,
                    os_name = ?,
                    os_version = ?,
                    app_version = ?,
                    updated_date = ?,
                    last_seen = ?
                WHERE hardware_uuid = ?
                """;

        jdbcTemplate.update(
                sql,
                device.getHostname(),
                device.getMacAddress(),
                device.getIpAddress(),
                device.getOsName(),
                device.getOsVersion(),
                device.getAppVersion(),
                Timestamp.valueOf(device.getUpdatedDate()),
                Timestamp.valueOf(device.getLastSeen()),
                device.getHardwareUuid()
        );
    }

    public List<Device> findAll() {

        String sql = """
                SELECT *
                FROM device
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}