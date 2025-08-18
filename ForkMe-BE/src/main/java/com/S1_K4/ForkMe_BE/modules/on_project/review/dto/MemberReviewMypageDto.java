package com.S1_K4.ForkMe_BE.modules.on_project.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.dto
 * @fileName : MemberReviewMypageDto
 * @date : 2025-08-14
 * @description : 후기 대상자와 프로젝트, 리뷰를 담은 dto
 */
@AllArgsConstructor
@Getter
public class MemberReviewMypageDto {
    private Long projectPk;
    private String review;
}