package com.S1_K4.ForkMe_BE.modules.on_project.review.dto;

import lombok.*;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.dto
 * @fileName : MemberReviewRequest
 * @date : 2025-08-11
 * @description : 멤버 평가 생성 dto
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReviewRequest {

    private String review;
    private Long targetUserPk;

}