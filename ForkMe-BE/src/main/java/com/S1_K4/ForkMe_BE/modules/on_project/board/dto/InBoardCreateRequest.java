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

    private String title;
    private String content;

    // 실제 파일을 받는 필드들
    private List<MultipartFile> images; // 업로드된 이미지 파일
    private List<MultipartFile> files;  // 업로드된 일반 파일들

    private List<String> imageUrls; // 기존 이미지 URL (빈칸 포함 가능)
    private List<String> fileUrls;  // 기존 첨부파일 URL (빈칸 포함 가능)

    private List<FileInfoResponse> fileInfos; // 추가
}