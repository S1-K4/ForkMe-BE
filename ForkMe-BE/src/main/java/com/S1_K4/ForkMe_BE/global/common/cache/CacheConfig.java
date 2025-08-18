package com.S1_K4.ForkMe_BE.global.common.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.redis
 * @fileName : CacheConfig
 * @date : 2025-08-18
 * @description : Redis를 이용한 Cache처리 관련 설정입니다.
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {
    @Bean
    public RedisCacheConfiguration defaultRedisCacheConfiguration() {
        //캐시에 들어갈 object->json 직렬화 시 사용할 전용 objectMapper
        ObjectMapper cacheOm = new ObjectMapper()
                //LocalDate, LocalDateTime같은 dateTime 지원
                .registerModule(new JavaTimeModule())
                //날짜를 숫자(timestamp)로 쓰지않고 문자열로 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //redis에서 역직렬화 시 DTO 타입을 알 수 있도록 타입 정보 포함 -> 안하면 LinkedHashMap으로만 읽혀서 에러 발생
        cacheOm.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        //redis value serializer(값 직렬화기)
        //object -> json 문자열 -> redis 저장
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(cacheOm);

        //redis 캐시 전역 설정 반환
        return RedisCacheConfiguration.defaultCacheConfig()
                //key 직렬화 -> 문자열(forkme:project:list:123)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                //value 직렬화 -> json(타입 정보 포함)
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(valueSerializer))
                //null값은 캐싱하지 않는다.
                .disableCachingNullValues()
                .prefixCacheNameWith("forkme:") //접두어
                .entryTtl(Duration.ofMinutes(5));   //기본 TTL : 5분
    }

    // 캐시 이름별 TTL 분리
    @Bean
    @Primary                        //redis를 캐시저장소로 쓰는 RedisCacheManager를 만든다.
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     RedisCacheConfiguration defaultConf) {

        //캐시 이름별로 다른 설정을 적용하기 위한 Map -> 특정 캐시이름별로 TTL을 다르게 줄 수 있다.
        Map<String, RedisCacheConfiguration> confs = new HashMap<>();
        //상세보기 TTL : 1분 -> 데이터 변경 주기가 상대적으로 길다.
        confs.put(CacheNames.PROJECT_DETAIL, defaultConf.entryTtl(Duration.ofMinutes(1)));
        //목록 TTL : 30초 -> 실시간성이 중요하므로 짧게
        confs.put(CacheNames.PROJECT_LIST,   defaultConf.entryTtl(Duration.ofMinutes(10)));


        return RedisCacheManager.builder(connectionFactory)
                //캐시 이름 별로 별도 설정이 없는 경우 기본값(5분 TTL)을 적용
                .cacheDefaults(defaultConf)
                //특정 캐시 이름을 설정한 경우 각 TTL 적용
                .withInitialCacheConfigurations(confs)
                //트랜잭션 롤백 시, 캐시 반영도 취소
                .transactionAware() // 트랜잭션 커밋 후 반영
                .build();
    }
}