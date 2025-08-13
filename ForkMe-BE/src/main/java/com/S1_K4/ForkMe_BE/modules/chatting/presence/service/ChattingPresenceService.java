package com.S1_K4.ForkMe_BE.modules.chatting.presence.service;

/**
 * @author : 김남이
 * @packageName : com.S1_K4.ForkMe_BE.modules.chatting.presence.service
 * @fileName : ChattingPresenceService
 * @date : 2025-08-12
 * @description : 채팅에서 접속자 현황을 관리하는 서비스 선언
 */
public interface ChattingPresenceService {

    void onSubscribe(String sessionId, Long roomPk, Long userPk);

    Long onUnsubscribeOrDisconnect(String sessionId); // [변경] 반환형: 방 PK를 돌려주도록

    boolean isOnline(Long roomPk, Long userPk);

}