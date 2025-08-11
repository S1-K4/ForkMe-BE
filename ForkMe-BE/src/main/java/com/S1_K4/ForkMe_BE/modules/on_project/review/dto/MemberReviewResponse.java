package com.S1_K4.ForkMe_BE.modules.on_project.review.dto;

import com.S1_K4.ForkMe_BE.modules.on_project.review.entity.MemberReview;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.dto
 * @fileName : MemberReviewResponse
 * @date : 2025-08-11
 * @description : 멤버 평가 응답 dto
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReviewResponse {

    private Long memberReviewPk;
    private String review;
    private Long writerUserPk;
    private Long targetUserPk;
    private Long projectPk;

    // ✅ 정적 변환 메서드 추가
    public static MemberReviewResponse from(MemberReview r) {
        return MemberReviewResponse.builder()
                .memberReviewPk(r.getMemberReviewPk())
                .review(r.getReview())
                .writerUserPk(r.getWriter().getUserPk())
                .targetUserPk(r.getTarget().getUserPk())
                .projectPk(r.getProject().getProjectPk())
                .build();
    }

    // ✅ 리스트 변환용 메서드 추가
    public static List<MemberReviewResponse> fromList(List<MemberReview> reviews) {
        return reviews.stream()
                .map(MemberReviewResponse::from)
                .collect(Collectors.toList());
    }


}