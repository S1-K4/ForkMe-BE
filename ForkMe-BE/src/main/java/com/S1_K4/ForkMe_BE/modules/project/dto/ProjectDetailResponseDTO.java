package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectDetailResponseDTO
 * @date : 2025-08-05
 * @description : 프로젝트 상세 조회 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@Jacksonized
@Schema(description = "프로젝트 상세 조회 DTO")
public class ProjectDetailResponseDTO {
    @Schema(description = "프로젝트 PK")
    private Long projectPk;

    @Schema(description = "프로젝트 프로필 PK")
    private Long projectProfilePk;

    @Schema(description = "프로젝트 팀장 userPK")
    private Long userPk;

    @Schema(description = "프로젝트 팀장 닉네임")
    private String nickname;

    @Schema(description = "프로젝트 프로필 명")
    private String projectProfileTitle;

    @Schema(description = "프로젝트 프로필 본문")
    private String projectProfileContent;

    @Schema(description = "프로젝트 진행 상황")
    private String projectStatus;

    @Schema(description = "프로젝트 진행 방식")
    private String progressType;

    @Schema(description = "프로젝트 모집 분야")
    private List<PositionResponseDTO> positions;

    @Schema(description = "프로젝트 기술 스택")
    private List<TechStackResponseDTO> techStacks;

    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;

    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate;

    @Schema(description = "프로젝트 시작 일정")
    private LocalDate projectStartDate;

    @Schema(description = "프로젝트 종료 일정")
    private LocalDate projectEndDate;

    @Schema(description = "예상 모집 인원")
    private int expectedMembers;

    @Schema(description = "좋아요 수")
    private Long likeCount;

    @Schema(description = "댓글 리스트")
    private List<CommentDTO> comments;

    @Schema(description = "이미지 리스트")
    private List<ProjectImageDTO> images;

    @Getter
    public static class CommentDTO {
        private Long commentPk;
        private String comment;
        private String nickname;
        private Long userPk;
        private Long parentPk;

        @Builder
        public CommentDTO(String comment, String nickname, Long parentPk, Long commentPk,Long userPk) {
            this.commentPk = commentPk;
            this.comment = comment;
            this.nickname = nickname;
            this.parentPk = parentPk;
            this.userPk = userPk;
        }

        public static CommentDTO toDTO(Comment comment) {
            return CommentDTO.builder()
                    .comment(comment.getComment())
                    .nickname(comment.getUser().getNickname())
                    .userPk(comment.getUser().getUserPk())
                    .parentPk(comment.getParent() != null ? comment.getParent().getCommentPk() : null)
                    .commentPk(comment.getCommentPk())
                    .build();
        }
    }
}