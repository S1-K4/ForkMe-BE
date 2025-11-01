package com.S1_K4.ForkMe_BE.global.searhlog.kafka;

import com.S1_K4.ForkMe_BE.global.searhlog.dto.SearchLogMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Kafka로 메시지(=이벤트)를 보낼 때 필요한 설정을 해주는 Spring @Configuration 클래
 * @fileName        : KafkaProducerConfig
 * @author          : 선순주
 * @since           : 2025-10-31
 *
 */
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    // Kafka로 메시지를 보낼 때 사용하는 프로듀서(Producer) 객체를 만드는 팩토리
    // key 타입은 String, value 타입은 SearchLogMessage (우리가 보낼 데이터 타입)
    @Bean
    public ProducerFactory<String, SearchLogMessage> producerFactory() {
        // Kafka Producer 설정을 위한 Map 생성
        Map<String, Object> config = new HashMap<>();
        // Kafka 서버 주소를 등록
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        // 메시지의 key를 String으로 직렬화
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 메시지의 value(SearchLogMessage)를 JSON 형태로 직렬화
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // 위 설정(Map)을 사용해서 ProducerFactory 객체 생성
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, SearchLogMessage> kafkaTemplate() {
        // 스프링에서 Kafka로 메시지 보낼 때 사용하는 핵심 객체에서 만든 ProducerFactory로 KafkaTemplate을 생성해서 Bean으로 등록
        return new KafkaTemplate<>(producerFactory());
    }
}