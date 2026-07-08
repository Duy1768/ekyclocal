package com.bank.ekyc.config;

import com.bank.ekyc.support.TrackingJdbcDriver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class HikariConnectionPoolTest {

    private String originalHousekeeperPeriodMs;

    @BeforeEach
    void setUp() {
        TrackingJdbcDriver.reset();
        originalHousekeeperPeriodMs = System.getProperty("com.zaxxer.hikari.housekeeping.periodMs");
    }

    @AfterEach
    void tearDown() {
        if (originalHousekeeperPeriodMs == null) {
            System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");
        } else {
            System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", originalHousekeeperPeriodMs);
        }
    }

    @Test
    void maximumPoolSize_doesNotCreateMoreThan20Connections() throws Exception {
        try (HikariDataSource dataSource = newDataSource(5, 20, 1_000, 600_000, 1_800_000)) {
            BorrowReport report = runContentionLoad(dataSource, 30, Duration.ofMillis(2_000));

            assertThat(report.successCount()).isEqualTo(20);
            assertThat(report.timeoutCount()).isEqualTo(10);
            assertThat(report.maxObservedTotalConnections()).isLessThanOrEqualTo(20);
            assertThat(TrackingJdbcDriver.createdConnections()).isLessThanOrEqualTo(20);
        }
    }

    @Test
    void minimumIdle_startsAtFiveAndScalesToTenUnderLoad() throws Exception {
        try (HikariDataSource dataSource = newDataSource(5, 20, 1_000, 600_000, 1_800_000)) {
            HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
            await(() -> poolMXBean.getTotalConnections() == 5 && poolMXBean.getIdleConnections() == 5,
                    Duration.ofSeconds(5),
                    "Pool did not reach minimum-idle=5 at startup");

            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch acquiredGate = new CountDownLatch(10);
            CountDownLatch releaseGate = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(10);

            try {
                List<Future<Boolean>> futures = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    futures.add(executor.submit(borrowAndHold(dataSource, startGate, acquiredGate, releaseGate)));
                }

                startGate.countDown();
                awaitLatch(acquiredGate, Duration.ofSeconds(5), "Not all requests acquired a connection");

                assertThat(poolMXBean.getTotalConnections()).isEqualTo(10);
                assertThat(poolMXBean.getActiveConnections()).isEqualTo(10);
                assertThat(poolMXBean.getIdleConnections()).isZero();

                releaseGate.countDown();
                for (Future<Boolean> future : futures) {
                    assertThat(future.get(5, TimeUnit.SECONDS)).isTrue();
                }
            } finally {
                releaseGate.countDown();
                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    void connectionTimeout_returnsTimeoutWhenPoolIsExhausted() throws Exception {

        try (HikariDataSource dataSource =
                     newDataSource(5, 20, 1_000, 600_000, 1_800_000)) {

            BorrowReport report =
                    runContentionLoad(dataSource, 25, Duration.ofMillis(2_000));

            assertThat(report.successCount()).isEqualTo(20);

            assertThat(report.timeoutCount()).isEqualTo(5);

            assertThat(report.timeoutMessages())
                    .isNotEmpty()
                    .allMatch(message ->
                            message.contains("Connection is not available")
                                    && message.contains("request timed out"));

            assertThat(report.timeoutMessages())
                    .anyMatch(message ->
                            message.matches(".*timed out after 10\\d\\dms.*"));
        }
    }

    @Test
    @Timeout(value = 70)
    void maxLifetime_replacesIdleConnectionAfterExpiry() throws Exception {
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "1000");

        try (HikariDataSource dataSource = newDataSource(1, 1, 1_000, 600_000, 30_000)) {
            HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

            await(() -> TrackingJdbcDriver.createdConnections() >= 1
                            && poolMXBean.getTotalConnections() == 1
                            && poolMXBean.getIdleConnections() == 1,
                    Duration.ofSeconds(5),
                    "Pool did not initialize with one idle connection");

            await(() -> TrackingJdbcDriver.createdConnections() >= 2
                            && TrackingJdbcDriver.closedConnections() >= 1,
                    Duration.ofSeconds(45),
                    "Idle connection was not replaced after maxLifetime");

            assertThat(poolMXBean.getTotalConnections()).isEqualTo(1);
            assertThat(poolMXBean.getIdleConnections()).isEqualTo(1);
        }
    }

    @Test
    @Timeout(value = 40)
    void idleTimeout_shrinksPoolBackToMinimumIdle() throws Exception {
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "1000");

        try (HikariDataSource dataSource = newDataSource(5, 20, 1_000, 10_000, 60_000)) {
            HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch acquiredGate = new CountDownLatch(20);
            CountDownLatch releaseGate = new CountDownLatch(1);
            ExecutorService executor = Executors.newFixedThreadPool(20);

            try {
                List<Future<Boolean>> futures = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    futures.add(executor.submit(borrowAndHold(dataSource, startGate, acquiredGate, releaseGate)));
                }

                startGate.countDown();
                awaitLatch(acquiredGate, Duration.ofSeconds(5), "Not all requests acquired a connection");

                assertThat(poolMXBean.getTotalConnections()).isEqualTo(20);
                assertThat(poolMXBean.getActiveConnections()).isEqualTo(20);

                releaseGate.countDown();
                for (Future<Boolean> future : futures) {
                    assertThat(future.get(5, TimeUnit.SECONDS)).isTrue();
                }

                await(() -> poolMXBean.getTotalConnections() == 5 && poolMXBean.getIdleConnections() == 5,
                        Duration.ofSeconds(20),
                        "Pool did not shrink back to minimum-idle=5 after idle timeout");
            } finally {
                releaseGate.countDown();
                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
            }
        }
    }

    private HikariDataSource newDataSource(
            int minimumIdle,
            int maximumPoolSize,
            long connectionTimeoutMs,
            long idleTimeoutMs,
            long maxLifetimeMs) {

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(TrackingJdbcDriver.class.getName());
        config.setJdbcUrl(TrackingJdbcDriver.URL_PREFIX + "pool");
        config.setPoolName("TEST_POOL");
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setConnectionTimeout(connectionTimeoutMs);
        config.setIdleTimeout(idleTimeoutMs);
        config.setMaxLifetime(maxLifetimeMs);
        config.setInitializationFailTimeout(1_000);
        config.setValidationTimeout(1_000);
        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    private BorrowReport runContentionLoad(
            HikariDataSource dataSource,
            int threadCount,
            Duration holdDuration) throws Exception {

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch releaseGate = new CountDownLatch(1);
        CountDownLatch acquiredGate = new CountDownLatch(Math.min(threadCount, 20));
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();

        try {
            List<Future<BorrowOutcome>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(borrowOnce(dataSource, startGate, releaseGate, acquiredGate)));
            }

            startGate.countDown();
            Thread.sleep(holdDuration.toMillis());
            releaseGate.countDown();

            int successCount = 0;
            int timeoutCount = 0;
            List<String> timeoutMessages = new ArrayList<>();
            for (Future<BorrowOutcome> future : futures) {
                BorrowOutcome outcome = future.get(5, TimeUnit.SECONDS);
                if (outcome.successful()) {
                    successCount++;
                } else {
                    timeoutCount++;
                    if (outcome.failureMessage() != null) {
                        timeoutMessages.add(outcome.failureMessage());
                    }
                }
            }

            int maxObservedTotalConnections = poolMXBean.getTotalConnections();
            return new BorrowReport(successCount, timeoutCount, timeoutMessages, maxObservedTotalConnections);
        } finally {
            releaseGate.countDown();
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private Callable<BorrowOutcome> borrowOnce(
            HikariDataSource dataSource,
            CountDownLatch startGate,
            CountDownLatch releaseGate,
            CountDownLatch acquiredGate) {

        return () -> {
            startGate.await();

            try (Connection connection = dataSource.getConnection()) {
                acquiredGate.countDown();
                releaseGate.await();
                return BorrowOutcome.completed();
            } catch (SQLTransientConnectionException e) {
                return BorrowOutcome.timedOut(e.getMessage());
            } catch (SQLException e) {
                return BorrowOutcome.timedOut(e.getMessage());
            }
        };
    }

    private Callable<Boolean> borrowAndHold(
            HikariDataSource dataSource,
            CountDownLatch startGate,
            CountDownLatch acquiredGate,
            CountDownLatch releaseGate) {

        return () -> {
            startGate.await();

            try (Connection connection = dataSource.getConnection()) {
                acquiredGate.countDown();
                releaseGate.await();
                return true;
            }
        };
    }

    private void await(BooleanSupplier condition, Duration timeout, String failureMessage) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(100);
        }

        fail(failureMessage);
    }

    private void awaitLatch(CountDownLatch latch, Duration timeout, String failureMessage) throws InterruptedException {
        if (!latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
            fail(failureMessage);
        }
    }

    private record BorrowOutcome(boolean successful, String failureMessage) {

        private static BorrowOutcome completed() {
            return new BorrowOutcome(true, null);
        }

        private static BorrowOutcome timedOut(String failureMessage) {
            return new BorrowOutcome(false, failureMessage);
        }
    }

    private record BorrowReport(
            int successCount,
            int timeoutCount,
            List<String> timeoutMessages,
            int maxObservedTotalConnections) {
    }
}
