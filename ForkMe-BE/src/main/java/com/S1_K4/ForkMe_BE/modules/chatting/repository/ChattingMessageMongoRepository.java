package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.mongo_document.ChattingMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.repository
 * @fileName : ChattingMessageMongoRepository
 * @date : 2025-08-07
 * @description : 채팅 메세지 mongoDB 저장용 레포지토리 입니다.
 */
@Repository
public interface ChattingMessageMongoRepository extends MongoRepository<ChattingMessageDocument, String> {

    //채팅 메세지 오래된 순 정렬
    List<ChattingMessageDocument> findByChattingRoomPkOrderByCreatedAtAsc(Long chattingRoomPk);

    // 참여 시간 이후 메시지 조회
    List<ChattingMessageDocument> findByChattingRoomPkAndCreatedAtAfterOrderByCreatedAtAsc(Long chattingRoomPk, LocalDateTime createdAt);


}