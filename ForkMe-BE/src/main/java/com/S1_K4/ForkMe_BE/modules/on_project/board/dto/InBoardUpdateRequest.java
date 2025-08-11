package com.S1_K4.ForkMe_BE.modules.on_project.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.dto
 * @fileName : InBoardUpdateRequest
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 수정 dto
 */

@Getter
@Setter
public class InBoardUpdateRequest {

    private String title;
    private Long projectPk;
    private String content;

    // 🗑️ 삭제 요청된 이미지 URL 목록
    private List<String> removedImageUrls;
    private List<String> removedFileUrls;

    // ✅ 기존 이미지 유지 목록 (선택사항, 없으면 백엔드에서 자동 계산)
    private List<String> imageUrls;

    // ✅ 기존 파일 유지 목록 (파일도 유지 관리한다면)
    private List<String> fileUrls;
}