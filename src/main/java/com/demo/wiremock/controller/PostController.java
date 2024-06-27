package com.demo.wiremock.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/posts")
public class PostController
{
    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public PostController(WebClient webClient, ObjectMapper objectMapper)
    {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ArrayNode getAllPosts()
    {
        return webClient
                .get()
                .uri("/posts")
                .retrieve()
                .bodyToMono(ArrayNode.class)
                .block();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JsonNode createNewPost(@RequestBody JsonNode body)
    {
        return webClient
                .post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @PutMapping("/{id}")
    public JsonNode updatePost(@RequestBody JsonNode body, @PathVariable Long id)
    {
        return webClient
                .put()
                .uri("/posts/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonNode> deletePost(@PathVariable Long id)
    {
        webClient
                .delete()
                .uri("/posts/" + id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return webClient
                .get()
                .uri("/posts/" + id)
                .retrieve()
                .toEntity(JsonNode.class)
                .onErrorResume(WebClientResponseException.BadRequest.class, e -> {
                    // Create an empty JSON object for the 404 response
                    JsonNode emptyJsonNode = objectMapper.createObjectNode();
                    return Mono.just(ResponseEntity.status(e.getStatusCode()).body(emptyJsonNode));
                })
                .block();
    }
}
