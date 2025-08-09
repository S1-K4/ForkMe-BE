package com.S1_K4.ForkMe_BE.modules.chatting.repository;

import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.repository
 * @fileName : ChattingMessageRepository
 * @date : 2025-08-07
 * @description : 채팅 메세지 MySQL 저장용 레포지토리 입니다.
 */
@Repository
public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, Long> {
}