package com.S1_K4.ForkMe_BE.global.searhlog.kafka;


import com.S1_K4.ForkMe_BE.global.searhlog.domain.SearchLogDocument;
import com.S1_K4.ForkMe_BE.global.searhlog.dto.SearchLogMessage;
import com.S1_K4.ForkMe_BE.global.searhlog.service.SearchLogEsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 *
 * 카프카에서 메세지를 꺼내서 엘라스틱서치로 넘기는 클래스
 * @fileName        : SearchLogConsumer
 * @author          : 선순주
 * @since           : 2025-10-31
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SearchLogConsumer {

    private final SearchLogEsService searchLogEsService;

    @KafkaListener(
     topics = "search-log", //구독한 토픽이름
     groupId = "search-log-group", //이 컨슈머가 어떤 컨슈머 그룹에 속하는지
     containerFactory = "kafkaListenerContainerFactory"   // 사용할 리스너 컨테이너 설정 Bean
    )
    public void consume(SearchLogMessage message){

        log.info("카프카에서 메시지 수신 : {}", message);

        //카프카에서 받은 메세지를 엘라스틱 전용 객체로 변환
        SearchLogDocument doc = SearchLogDocument.builder()
                .id(UUID.randomUUID().toString())
                .keyword(message.getKeyword())
                .searchedAt(message.getSearchedAt())
                .build();

        try {
            searchLogEsService.save(doc);
            log.info("Elasticsearch 저장 성공: {}", doc);
        } catch (Exception e) {
            log.error("Elasticsearch 저장 실패", e);
        }
    }
}