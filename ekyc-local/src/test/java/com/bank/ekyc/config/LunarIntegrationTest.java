package com.bank.ekyc.config;

import com.bank.ekyc.application.service.LunarService;
import com.bank.ekyc.presentation.response.LunarResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "third-party.lunar.base-url=http://localhost:8089"
})
class LunarIntegrationTest {

    static WireMockServer wireMockServer;

    @Autowired
    LunarService lunarService;

    @BeforeAll
    static void beforeAll() {

        wireMockServer = new WireMockServer(8089);

        wireMockServer.start();

        configureFor("localhost", 8089);

    }

    @AfterAll
    static void afterAll() {

        wireMockServer.stop();

    }

    @Test
    void shouldCallLunarApiSuccessfully() {

        stubFor(get(urlEqualTo("/api/today"))
                .willReturn(okJson("""
                {
                  "success": true,
                  "data": {
                    "solar": {
                      "day": 13,
                      "month": 7,
                      "year": 2026
                    },
                    "lunar": {
                      "day": 29,
                      "month": 5,
                      "year": 2026,
                      "isLeap": false,
                      "holiday": "",
                      "canChiDay": "Mậu Tý",
                      "canChiMonth": "Nhâm Ngọ",
                      "canChiYear": "Bính Ngọ",
                      "gioDauNgay": "Tý",
                      "tietKhi": "Tiểu Thử",
                      "gioHoangDao": [
                        {
                          "name": "Tý",
                          "time": "23:00-01:00"
                        },
                        {
                          "name": "Sửu",
                          "time": "01:00-03:00"
                        },
                        {
                          "name": "Mão",
                          "time": "05:00-07:00"
                        }
                      ]
                    },
                    "astrology": {},
                    "jd": 2461234
                  }
                }
                """)));

        LunarResponse result = lunarService.getToday();

        assertNotNull(result);
        assertEquals(29, result.getDay());
        assertEquals(5, result.getMonth());
    }

    @Test
    void shouldTimeout() {

        stubFor(get("/api/today")
                .willReturn(aResponse()
                        .withFixedDelay(15000)
                        .withBody("{}")));

        assertThrows(Exception.class,
                () -> lunarService.getToday());

    }

    @Test
    void shouldUseConnectionPool() throws Exception {

        stubFor(get(urlEqualTo("/api/today"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withFixedDelay(5000)
                        .withBody("""
                    {
                      "success": true,
                      "data": {
                        "solar": {
                          "day": 13,
                          "month": 7,
                          "year": 2026
                        },
                        "lunar": {
                          "day": 29,
                          "month": 5,
                          "year": 2026,
                          "isLeap": false,
                          "holiday": "",
                          "canChiDay": "Mậu Tý",
                          "canChiMonth": "Nhâm Ngọ",
                          "canChiYear": "Bính Ngọ",
                          "gioDauNgay": "Tý",
                          "tietKhi": "Tiểu Thử",
                          "gioHoangDao": [
                            {
                              "name":"Tý",
                              "time":"23:00-01:00"
                            }
                          ]
                        },
                        "astrology": {},
                        "jd": 2461234
                      }
                    }
                    """)));

        int requestCount = 75;

        ExecutorService executor = Executors.newFixedThreadPool(requestCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(requestCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < requestCount; i++) {

            futures.add(executor.submit(() -> {

                try {

                    startLatch.await();

                    LunarResponse response = lunarService.getToday();

                    assertNotNull(response);

                    success.incrementAndGet();

                } catch (Exception e) {

                    failed.incrementAndGet();

                    System.out.println(e.getMessage());

                } finally {

                    finishLatch.countDown();

                }

                return null;

            }));

        }

        long start = System.currentTimeMillis();

        startLatch.countDown();

        assertTrue(finishLatch.await(2, TimeUnit.MINUTES));

        for (Future<?> future : futures) {
            future.get();
        }

        executor.shutdown();

        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        long duration = System.currentTimeMillis() - start;

        System.out.println("=================================");
        System.out.println("Success : " + success.get());
        System.out.println("Failed  : " + failed.get());
        System.out.println("Duration: " + duration + " ms");
        System.out.println("=================================");

        assertEquals(requestCount, success.get());
        assertEquals(0, failed.get());

    }
}
