package com.S1_K4.ForkMe_BE.modules.on_project.board.dto;

import lombok.*;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.dto
 * @fileName : FileInfoResponse
 * @date : 2025-08-08
 * @description : 다운로드 파일 정보 저장 dto
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoResponse {
    private String fileUrl;             // S3에 저장된 URL
    private String originalFileName;
}