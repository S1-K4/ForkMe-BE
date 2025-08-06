package com.S1_K4.ForkMe_BE.modules.on_project.board.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.dto
 * @fileName : InBoardCreateRequest
 * @date : 2025-08-05
 * @description : 워크스페이스 게시판에서 게시글 생성할 때 필요한 dto
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InBoardCreateRequest {

    private String teamName;
    private String title;
    private String content;

    private List<MultipartFile> images; // 이미지
    private List<MultipartFile> files; // 첨부파일
}