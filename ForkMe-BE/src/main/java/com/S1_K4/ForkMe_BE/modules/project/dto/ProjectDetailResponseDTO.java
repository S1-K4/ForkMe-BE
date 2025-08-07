package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ProjectDetailResponseDTO {
    //프로젝트 pk
    private Long projectPk;

    //프로젝트 프로필 pk
    private Long projectProfilePk;

    //프로젝트 팀장 pk
    private Long userPk;

    //프로젝트 팀장닉네임
    private String nickname;

    //프로젝트 프로필 제목
    private String projectProfileTitle;

    //프로젝트 프로필 본문
    private String projectProfileContent;

    //프로젝트 진행상황
    private String projectStatus;

    //프로젝트 진행방식
    private String progressType;

    //프로젝트 모집 분야
    private List<PositionResponseDTO> positions;

    //프로젝트 기술 스택
    private List<TechStackResponseDTO> techStacks;

    //모집 시작일
    private LocalDate recruitmentStartDate;

    //모집 마감일
    private LocalDate recruitmentEndDate;

    //프로젝트 시작 일정
    private LocalDate projectStartDate;

    //프로젝트 종료 일정
    private LocalDate projectEndDate;

    //예상 모집 인원
    private int expectedMembers;

    //좋아요 수
    private Long likeCount;
    
    //댓글 리스트
    private List<CommentDTO> comments;

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