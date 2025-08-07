package com.S1_K4.ForkMe_BE.global.common.redis;

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


        // LocalDateTime 직렬화를 위한 ObjectMapper 설정 추가
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 포맷 유지

        // 기존 Jackson2JsonRedisSerializer 생성 방식 유지하되, ObjectMapper 만 커스터마이징
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 직렬화기 설정
        jsonRedisTemplate.setKeySerializer(new StringRedisSerializer());
        jsonRedisTemplate.setValueSerializer(serializer);
        jsonRedisTemplate.setHashKeySerializer(new StringRedisSerializer());  // 해시 사용 시에도 동일한 직렬화기 설정
        jsonRedisTemplate.setHashValueSerializer(serializer);

        return jsonRedisTemplate;
    }

    /** 채팅용 리스너 **/
    //  Redis Subscriber 가 "chat" 채널을 구독하게 설정
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber redisSubscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(redisSubscriber, new ChannelTopic("chat"));
        return container;
    }

}
