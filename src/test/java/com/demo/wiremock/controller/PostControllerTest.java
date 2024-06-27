package com.demo.wiremock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerTest
{
    @Autowired
    private WebTestClient webTestClient;

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
    public void getAllPosts()
    {
        webTestClient
                .get()
                .uri("/api/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].userId").isEqualTo(1)
                .jsonPath("$[1].userId").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[1].id").isEqualTo(2);
    }

    @Test
    public void createNewPost() throws JsonProcessingException
    {
        String jsonString = "{\n        \"userId\": 1,\n  \"title\": \"foo\",\n        \"body\": \"bar\"\n    }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        webTestClient
                .post()
                .uri("/api/posts")
                .bodyValue(actualObj)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("foo")
                .jsonPath("$.body").isEqualTo("bar")
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.id").isEqualTo(101);
    }

    @Test
    public void updatePost() throws JsonProcessingException
    {
        String jsonString = "{\n        \"userId\": 1,\n \"id\": 1,\n  \"title\": \"foo\",\n        \"body\": \"bar\"\n    }";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        webTestClient
                .put()
                .uri("/api/posts/10")
                .bodyValue(actualObj)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("foo")
                .jsonPath("$.body").isEqualTo("bar")
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    public void deletePost()
    {
        webTestClient
                .delete()
                .uri("/api/posts/1")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
