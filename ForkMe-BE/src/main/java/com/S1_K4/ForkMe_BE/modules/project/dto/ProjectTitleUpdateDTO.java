package com.S1_K4.ForkMe_BE.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectTitleUpdateDTO
 * @date : 2025-08-13
 * @description :ProjectTitleUpdateDTO
 */
@Schema(description = "프로젝트 명 변경 DTO")
public record ProjectTitleUpdateDTO (String projectTitle){
}