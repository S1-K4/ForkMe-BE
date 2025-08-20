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

    // gpt-4o-mini 권장 (비용/속도/품질 밸런스)
    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    /** 시스템 컨텍스트를 포함해서 완성도 높은 답변을 받도록 */
    public String complete(String prompt, String systemCtx) {
        // 1) Chat Completions 먼저 시도
        String c = callChatCompletions(prompt, systemCtx);
        if (c != null) return c;

        // 2) (선택) Responses API 폴백
        String r = callResponses(prompt);
        if (r != null) return r;

        return "❗LLM 호출 중 오류가 발생했습니다.";
    }

    private String callChatCompletions(String prompt, String systemCtx) {
        try {
            var body = mapper.createObjectNode();
            body.put("model", model);
            body.put("temperature", 0.7);   // 살짝 창의성
            body.put("max_tokens", 5000);    // 길이 확보 (필요 시 조정)

            var messages = body.putArray("messages");
            messages.addObject().put("role", "system").put("content", systemCtx);
            messages.addObject().put("role", "user").put("content", prompt);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> res = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() / 100 != 2) {
                System.err.println("[ChatCompletions] status=" + res.statusCode() + " body=" + res.body());
                return null;
            }

            JsonNode root = mapper.readTree(res.body());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText(null);
            }
            System.err.println("[ChatCompletions] Unexpected body=" + res.body());
            return null;
        } catch (Exception e) {
            System.err.println("[ChatCompletions] Exception: " + e.getMessage());
            return null;
        }
    }

    /** 옵션: Responses API 폴백 (있으면 도움됨) */
    private String callResponses(String prompt) {
        try {
            var body = mapper.createObjectNode();
            body.put("model", model);
            body.put("input", prompt);

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/responses"))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> res = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());

            if (res.statusCode() / 100 != 2) {
                System.err.println("[ResponsesAPI] status=" + res.statusCode() + " body=" + res.body());
                return null;
            }

            JsonNode root = mapper.readTree(res.body());
            if (root.hasNonNull("output_text")) {
                return root.get("output_text").asText();
            }
            if (root.has("output") && root.get("output").isArray() && root.get("output").size() > 0) {
                JsonNode first = root.get("output").get(0);
                if (first.has("content") && first.get("content").isArray() && first.get("content").size() > 0) {
                    JsonNode content0 = first.get("content").get(0);
                    if (content0.has("text")) return content0.get("text").asText();
                }
            }
            System.err.println("[ResponsesAPI] Unexpected body=" + res.body());
            return null;
        } catch (Exception e) {
            System.err.println("[ResponsesAPI] Exception: " + e.getMessage());
            return null;
        }
    }
}