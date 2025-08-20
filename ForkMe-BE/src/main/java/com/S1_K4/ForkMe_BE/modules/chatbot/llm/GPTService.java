package com.S1_K4.ForkMe_BE.modules.chatbot.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.llm
 * @fileName : GPTService
 * @date : 2025-08-20
 * @description : LLM 호출 : RestApi + output_text 활용
 */

@Service
@RequiredArgsConstructor
public class GPTService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String openaiApiKey;

    // 필요 시 모델을 yml에서 교체 가능: gpt-4o, gpt-4o-mini 등
    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    public String complete(String prompt) {
        try {
            var body = mapper.createObjectNode();
            body.put("model", model);
            body.put("input", prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            // 1순위: output_text (Responses API의 집계 텍스트)
            if (root.hasNonNull("output_text")) {
                return root.get("output_text").asText();
            }

            // 2순위: output 배열 내부 텍스트 (호환)
            if (root.has("output") && root.get("output").isArray() && root.get("output").size() > 0) {
                JsonNode first = root.get("output").get(0);
                if (first.has("content") && first.get("content").isArray() && first.get("content").size() > 0) {
                    JsonNode content0 = first.get("content").get(0);
                    if (content0.has("text")) return content0.get("text").asText();
                }
            }

            return "❗LLM 응답을 파싱하지 못했습니다. 잠시 후 다시 시도해주세요.";
        } catch (Exception e) {
            return "❗LLM 호출 중 오류가 발생했습니다.";
        }
    }
}