package com.S1_K4.ForkMe_BE.modules.apply.dto;

import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyDto
 * @date : 2025-08-11
 * @description : 지원한 내역 반환 dto
 */
@Getter
@Setter
@AllArgsConstructor
public class MyApplyListResponseDto {
    private Long applyPk;
    private String content;
    private ApplyStatus state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long userPk;
    private Long projectPk;
    private String projectTitle;

    private Long positionPk;
    private String positionName;

    private List<TechStackResponseDTO> techStacks;

    public MyApplyListResponseDto(Long applyPk, String content, ApplyStatus state, LocalDateTime createdAt, LocalDateTime updatedAt, Long userPk, Long projectPk, String projectTitle, Long positionPk, String positionName) {
        this.applyPk = applyPk;
        this.content = content;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userPk = userPk;

        this.projectPk = projectPk;
        this.projectTitle = projectTitle;

        this.positionPk = positionPk;
        this.positionName = positionName;
    }
}