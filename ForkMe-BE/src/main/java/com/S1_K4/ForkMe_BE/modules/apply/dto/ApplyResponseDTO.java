package com.S1_K4.ForkMe_BE.modules.apply.dto;

import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.dto
 * @fileName : ApplyResponseDTO
 * @date : 2025-08-12
 * @description : ApplyResponseDTO
 */
@Getter
@Builder
public class ApplyResponseDTO {
    @Schema(description = "신청서 PK", example = "3")
    private Long applyPk;
    @Schema(description = "프로젝트 PK", example = "1")
    private Long projectPk;
    @Schema(description = "유저 PK", example = "2")
    private Long userPk;
    @Schema(description = "유저 닉네임", example = "test3")
    private String nickname;
    @Schema(description = "신청서 한줄 소개", example = "안녕하세요 백엔드 개발자로 지원합니다.")
    private String content;
    @Schema(description = "지원한 모집 분야", example = "백엔드")
    private String positionName;
    @Schema(description = "지원한 기술 스택")
    private List<TechStackInfo> techStacks;        // 신청서에 포함된 기술스택
    @Schema(description = "유저의 기술 스택")
    private List<TechStackInfo> userTechStacks;    // 유저의 전체 기술스택
    @Schema(description = "신청서 상태", example = "수락")
    private String status;
    @Schema(description = "작성일자", example = "2025-07-22")
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class TechStackInfo {
        private Long techPk;
        private String techName;

        public static TechStackInfo from(TechStack techStack) {
            return TechStackInfo.builder()
                    .techPk(techStack.getTechPk())
                    .techName(techStack.getTechName())
                    .build();
        }
    }

    public static ApplyResponseDTO from(Apply apply) {
        ProjectPosition position = apply.getProjectPosition();

        return ApplyResponseDTO.builder()
                .applyPk(apply.getApplyPk())
                .projectPk(apply.getProject().getProjectPk())
                .userPk(apply.getUser().getUserPk())
                .nickname(apply.getUser().getNickname())
                .content(apply.getContent())
                .positionName(position.getPosition().getPositionName())
                .techStacks(
                        apply.getApplyTechStacks().stream()
                                .map(ats -> TechStackInfo.from(ats.getTechStack()))
                                .toList()
                )
                .userTechStacks(
                        apply.getUser().getUserTechStacks().stream()
                                .map(uts -> TechStackInfo.from(uts.getTechStack()))
                                .toList()
                )
                .status(apply.getStatus().name())
                .createdAt(apply.getCreatedAt())
                .build();
    }
}
