package com.S1_K4.ForkMe_BE.global.searhlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.searhlog.dto
 * @fileName : SearchLogMessage
 * @date : 2025-10-31
 * @description : kafka로 주고받는 메시지 포멧
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogMessage {
    private String keyword;     //검색한 키워드
    private String searchedAt;  //검색한 시간
}