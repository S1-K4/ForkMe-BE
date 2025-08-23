package com.S1_K4.ForkMe_BE.modules.chatbot.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Document(collection = "chat_session_summaries")
public class ChatSessionSummary {
    @Id
    private String id;

    private String sessionId;

    private String techStack;
    private String duration;
    private String members;
    private List<String> ideas;

    private Instant startedAt;
    private Instant firstIdeasAt;
    private Instant endedAt;

    // TTL 인덱스 (expiresAt 필드 기준)
    private Date expiresAt;
}