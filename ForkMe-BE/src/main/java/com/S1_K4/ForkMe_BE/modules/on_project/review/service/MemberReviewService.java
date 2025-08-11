package com.S1_K4.ForkMe_BE.modules.on_project.review.service;

import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewResponse;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.service
 * @fileName : MemberReviewService
 * @date : 2025-08-11
 * @description : 멤버 평가 service
 */
public interface MemberReviewService {

    MemberReviewResponse createReview(Long projectPk, Long writerUserPk, MemberReviewRequest dto);
    List<MemberReviewResponse> getMyWrittenReviews(Long userPk, Long projectPk); //내가 작성한 리뷰
    List<MemberReviewResponse> getMyReceivedReviews(Long userPk, Long projectPk); //내가 받은 리뷰
}
