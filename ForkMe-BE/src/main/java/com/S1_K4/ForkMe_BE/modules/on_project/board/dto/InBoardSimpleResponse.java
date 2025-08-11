package com.S1_K4.ForkMe_BE.modules.on_project.board.dto;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.dto
 * @fileName : InBoardSimpleResponse
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 전체 조회 dto
 */

@Getter
@Builder
public class InBoardSimpleResponse {

    private Long boardInProjectPk;
    private String title;
    private String content;
    private LocalDateTime createdAt;


    public static InBoardSimpleResponse from(BoardInProject board) {
        return InBoardSimpleResponse.builder()
                .boardInProjectPk(board.getBoardInProjectPk())
                .title(board.getTitle())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .build();
    }
}