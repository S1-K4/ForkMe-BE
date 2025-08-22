package com.S1_K4.ForkMe_BE.modules.chatbot.repository;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.ChatSessionSummary;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.repository
 * @fileName : ChatSessionSummaryRepository
 * @date : 2025-08-22
 * @description :
 */
public interface ChatSessionSummaryRepository
        extends MongoRepository<ChatSessionSummary, String> {
    Optional<ChatSessionSummary> findBySessionId(String sessionId);
}
