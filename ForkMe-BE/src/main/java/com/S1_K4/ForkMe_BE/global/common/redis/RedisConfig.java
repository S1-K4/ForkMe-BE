package com.S1_K4.ForkMe_BE.global.common.redis;

import com.S1_K4.ForkMe_BE.modules.chatbot.domain.BotSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.TimeZone;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : RedisConfig
 * @date : 2025-08-04
 * @description : Redis 관련 설정입니다.
 */

@Configuration
public class RedisConfig {

    /** redisTemplate - from.관중 **/
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    /** jsonRedisTemplate - from.남이 **/
    @Bean
    public RedisTemplate<String, Object> jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> jsonRedisTemplate = new RedisTemplate<>();
        jsonRedisTemplate.setConnectionFactory(redisConnectionFactory); //주입 방식


        //LocalDateTime 직렬화를 위한 ObjectMapper 설정 추가
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule()) // LocalDateTime 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISO-8601 포맷 유지
                .setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // Redis 직렬화/역직렬화 시 날짜·시간이 항상 KST 시간 포맷 고정

        //기존 Jackson2JsonRedisSerializer 생성 방식 유지하되, ObjectMapper 만 커스터마이징
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        //직렬화기 설정
        jsonRedisTemplate.setKeySerializer(new StringRedisSerializer());
        jsonRedisTemplate.setValueSerializer(serializer);
        jsonRedisTemplate.setHashKeySerializer(new StringRedisSerializer());  // 해시 사용 시에도 동일한 직렬화기 설정
        jsonRedisTemplate.setHashValueSerializer(serializer);

        return jsonRedisTemplate;
    }

    // Redis Subscriber 가 "chat" 채널을 구독하게 설정
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber redisSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, new ChannelTopic("chat"));
        return container;
    }

    //챗봇 세션 객체를 redis에 타입 안정적으로 넣고빼는 템플릿(세션 저장, ttl)
    @Bean
    public RedisTemplate<String, BotSession> botSessionRedisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, BotSession> t = new RedisTemplate<>();
        t.setConnectionFactory(cf);

        // ObjectMapper 커스터마이징
        ObjectMapper om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<BotSession> ser =
                new Jackson2JsonRedisSerializer<>(om, BotSession.class);

        t.setKeySerializer(new StringRedisSerializer());
        t.setValueSerializer(ser);
        t.setHashKeySerializer(new StringRedisSerializer());
        t.setHashValueSerializer(ser);

        t.afterPropertiesSet();
        return t;
    }

}
