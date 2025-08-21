package com.S1_K4.ForkMe_BE.modules.alarm.repository;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.alarm.mongo_document.AlarmMessageDocument;
import com.S1_K4.ForkMe_BE.modules.chatting.mongo_document.ChattingMessageDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.alarm.repository
 * @fileName : AlarmMessageMongoRepository
 * @date : 2025-08-20
 * @description : 알람 메세지 mongoDB 저장용 레포지토리 입니다.
 */
public interface AlarmMessageMongoRepository extends MongoRepository<AlarmMessageDocument, String> {

    // 유저별 삭제되지 않은 알림 최신순 조회(페이징 지원)
    List<AlarmMessageDocument> findByUserPkAndDeletedYnOrderByCreatedAtDesc(Long userPk, Yn deletedYn, Pageable pageable);

    //삭제되지 않으면서 아직 안읽은 알람 조회
    List<AlarmMessageDocument> findByUserPkAndDeletedYnAndReadYn(Long userPk, Yn deletedYn, Yn readYn);

    // 삭제되지 않은 알람 조회
    List<AlarmMessageDocument> findByUserPkAndDeletedYn(Long userPk, Yn deletedYn);

    // 미확인 알람 개수 조회 (userPk와 readYn 기준)
    long countByUserPkAndReadYnAndDeletedYn(Long userPk, Yn readYn, Yn deletedYn);


}
