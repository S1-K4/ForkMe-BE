package com.S1_K4.ForkMe_BE.global.searhlog.kafka;

import com.S1_K4.ForkMe_BE.global.searhlog.dto.SearchLogMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Kafka에서 메시지를 받아오기 위한 설정을 담당하는 Spring @Configuration 클래스
 * @fileName        : KafkaConsumerConfig
 * @author          : 선순주
 * @since           : 2025-10-31
 *
 */
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    // ConsumerFactory는 Kafka로부터 메시지를 꺼낼 때 필요한 '소비자' 객체를 만드는 팩토리
    @Bean
    public ConsumerFactory<String, SearchLogMessage> consumerFactory() {
        // Kafka로부터 받은 메시지를 SearchLogMessage 객체로 자동 변환(역직렬화) 해주는 역할
        JsonDeserializer<SearchLogMessage> deserializer = new JsonDeserializer<>(SearchLogMessage.class);
        // 메시지의 타입 정보를 헤더에서 제거할지 설정하는 부분(false -> 타입 정보가 유지되어 타입 매핑이 안전)
        deserializer.setRemoveTypeHeaders(false);
        // 역직렬화(메시지 → 객체)할 때 어떤 패키지의 클래스까지 허용할지 설정 (현재는 모든 패키지 허용)
        deserializer.addTrustedPackages("com.project.nyang.modules.board", "com.project.nyang.modules.animal");
        // Kafka 메시지의 Key에 대해서도 타입 매퍼를 사용하겠다는 옵션
        deserializer.setUseTypeMapperForKey(true);

        // Kafka Consumer의 필수 옵션들을 담는 Map
        Map<String, Object> config = new HashMap<>();
        // kafka 서버의 주소(포트 포함)**를 지정
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        // 이 Consumer가 속한 Consumer Group 이름
        // 같은 Group 내 Consumer들이 서로 메시지를 분배해서 소비
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "search-log-group");
        // **Key(메시지의 식별자)**를 어떻게 읽을지 지정 (여기선 String)
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        //제로 Kafka Consumer 객체를 만들어주는 Factory를 리턴
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    // @KafkaListener에서 사용할 Listener 컨테이너 팩토리 Bean을 등록.
    // 여러 개의 Kafka Consumer가 동시에 메시지를 병렬로 처리할 수 있게 해주는 설정 -> 위에서 만든 consumerFactory를 연결시킴.
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SearchLogMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SearchLogMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    /**
      이 코드는 Kafka에서 메시지를 읽어오는 설정을 담당합니다.
      메시지는 JSON → SearchLogMessage 객체로 자동 변환됩니다.
      ConsumerFactory는 “실제 메시지 꺼내는 소비자(Consumer)”를 만들어주고,
      ListenerContainerFactory는 “여러 소비자가 동시에 메시지를 처리할 수 있도록 도와주는 역할”을 합니다.
     **/
}