package com.S1_K4.ForkMe_BE.modules.chatbot.setting;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.ChatSessionSummary;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatbot.setting
 * @fileName : MongoTtlIndexConfig
 * @date : 2025-08-22
 * @description :
 */
@Configuration
public class MongoTtlIndexConfig implements ApplicationListener<ContextRefreshedEvent> {

    private final MongoTemplate template;

    public MongoTtlIndexConfig(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        IndexOperations ops = template.indexOps(ChatSessionSummary.class);
        // TTL 인덱스 생성(이미 동일 인덱스가 있으면 MongoDB가 재생성하지 않음)
        ops.createIndex(new Index().on("expiresAt", Sort.Direction.ASC).expire(0));
    }
}