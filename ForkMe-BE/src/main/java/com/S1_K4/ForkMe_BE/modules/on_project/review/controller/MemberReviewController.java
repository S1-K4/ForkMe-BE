package com.S1_K4.ForkMe_BE.modules.on_project.review.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.review.service.MemberReviewService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.controller
 * @fileName : MemberReviewController
 * @date : 2025-08-11
 * @description : 멤버 평가 controller
 */

@RequiredArgsConstructor
@RequestMapping("/api/member_review")
@RestController
public class MemberReviewController {

    private final MemberReviewService memberReviewService;
    // 리뷰 작성
    @PostMapping("/projects/{projectPk}/create")
    public ResponseEntity<MemberReviewResponse> createReview(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MemberReviewRequest dto) {

        Long writerUserPk = userDetails.getUserPk();
        MemberReviewResponse responseDTO = memberReviewService.createReview(projectPk, writerUserPk, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 특정 프로젝트 내가작성한 리뷰 조회
    @GetMapping("/written/projects/{projectPk}")
    public ResponseEntity<?> getMyWrittenReviews(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long writerUserPk = userDetails.getUserPk();
        List<MemberReviewResponse> reviews = memberReviewService.getMyWrittenReviews(writerUserPk, projectPk);
        return ResponseEntity.ok(reviews);
    }

    // 내가 작성한 모든 리뷰 조회
    @GetMapping("/written")
    public ResponseEntity<?> getMyWrittenAllReviews(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long writerUserPk= userDetails.getUserPk();

        List<MemberReviewResponse> reviews = memberReviewService.getMyWrittenAllReviews(writerUserPk);

        return  ResponseEntity.ok(reviews);
    }

    // 특정 프로젝트에서 내가 받은 리뷰
    @GetMapping("/received/projects/{projectPk}")
    public ResponseEntity<?> getMyReceivedReviews(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long receiveUserPk = userDetails.getUserPk();
        List<MemberReviewResponse> reviews = memberReviewService.getMyReceivedReviews(receiveUserPk, projectPk);
        return ResponseEntity.ok(reviews);
    }

    // 내가 받은 모든 리뷰
    @GetMapping("/received")
    public ResponseEntity<?> getMyReceivedAllReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long receiveUserPk = userDetails.getUserPk();
        List<MemberReviewResponse> reviews = memberReviewService.getMyReceivedAllReviews(receiveUserPk);
        return ResponseEntity.ok(reviews);
    }

}