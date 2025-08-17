package com.S1_K4.ForkMe_BE.modules.on_project.board.dto;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.dto
 * @fileName : InBoardDetailResponse
 * @date : 2025-08-05
 * @description : 워크스페이스 내 게시글 상세보기
 */

@Getter
@Builder
public class InBoardDetailResponse {

    private Long boardInProjectPk;
    private Long projectPk;
    private Long userPk;
    private String userNickname;

    private String title;
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<String> imageUrls;
    private List<FileInfoResponse> fileInfos; // 첨부파일 URL 리스트

    public static InBoardDetailResponse from(BoardInProject board, List<String> imageUrls, List<FileInfoResponse> fileInfos) {
        return InBoardDetailResponse.builder()
                .boardInProjectPk(board.getBoardInProjectPk())
                .projectPk(board.getProject().getProjectPk())
                .userPk(board.getUser().getUserPk())
                .userNickname(board.getUser().getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .imageUrls(imageUrls)
                .fileInfos(fileInfos)
                .build();
    }
}