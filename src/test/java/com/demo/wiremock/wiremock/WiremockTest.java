package com.demo.wiremock.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WiremockTest
{
    private static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock()
    {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock()
    {
        wireMockServer.stop();
    }


    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry dynamicPropertyRegistry)
    {
        dynamicPropertyRegistry.add("api.base.url", wireMockServer::baseUrl);
    }

    @Test
    void testWiremock()
    {
        System.out.println(wireMockServer.baseUrl());
        assertTrue(wireMockServer.isRunning());
    }
}