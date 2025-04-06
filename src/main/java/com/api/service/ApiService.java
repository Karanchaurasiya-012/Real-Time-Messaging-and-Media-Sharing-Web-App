package com.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ApiService {

    @Value("${spring.ai.openai.base-url}")
    private String API_URL;

    @Value("${spring.ai.openai.api-key}")
    private String API_KEY;

    public String getAnswer(String question) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek/deepseek-chat");
        body.put("messages", new Object[]{
                Map.of("role", "system", "content", "You are an AI assistant."),
                Map.of("role", "user", "content", question)
        });

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");

                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");
                    return content != null ? content.trim() : "Sorry, I couldn't find an answer.";
                }
            }

            return "Sorry, I couldn't find an answer.";

        } catch (Exception e) {
            return "Error occurred while fetching the answer: " + e.getMessage();
        }
    }
}
