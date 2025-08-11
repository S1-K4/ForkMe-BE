package com.S1_K4.ForkMe_BE.modules.apply.dto;

import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import lombok.Builder;
import lombok.Getter;

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
    private Long applyPk;
    private Long projectPk;
    private Long userPk;
    private String nickname;
    private String content;
    private String positionName;
    private List<TechStackInfo> techStacks;
    private String status;

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
                .status(apply.getStatus().name())
                .build();
    }
}
