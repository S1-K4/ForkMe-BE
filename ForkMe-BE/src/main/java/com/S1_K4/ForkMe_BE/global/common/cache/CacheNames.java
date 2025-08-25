package com.S1_K4.ForkMe_BE.global.common.cache;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.cache
 * @fileName : CacheNames
 * @date : 2025-08-18
 * @description : 캐시이름 상수 인터페이스
 */
public interface CacheNames {

        //프로젝트 상세 : 정적 파트(제목/본문/상태/포지션/스택/이미지/기간 등)
        String PROJECT_DETAIL_STATIC = "project:detail:static";

        //프로젝트 상세 : 댓글 목록
        String PROJECT_COMMENTS = "project:detail:comments";

        //프로젝트 상세 : 좋아요 카운트
        String PROJECT_LIKE_COUNT = "project:detail:likeCount";

        //프로젝트 목록
        String PROJECT_LIST   = "project:list";

        // 김송이 추가(워크스페이스)
        String BOARD_PROJECT_DETAIL = "board:project:detail";
        String BOARD_PROJECT_LIST = "board:project:list";
        String REVIEW_LIST = "review:list";
        String REVIEW_DETAIL = "review:detail";
        

}
