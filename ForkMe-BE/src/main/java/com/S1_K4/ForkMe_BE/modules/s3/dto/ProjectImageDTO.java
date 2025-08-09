package com.S1_K4.ForkMe_BE.modules.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.dto
 * @fileName : ProjectImageDTO
 * @date : 2025-08-09
 * @description : 이미지 dto
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectImageDTO {
    private Long s3ImagePk;
    private String url;
}