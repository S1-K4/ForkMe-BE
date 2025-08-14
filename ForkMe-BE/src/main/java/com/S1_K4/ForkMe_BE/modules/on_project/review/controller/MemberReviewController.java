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
@RequestMapping("/api/projects/{projectPk}/member_review")
@RestController
public class MemberReviewController {

    private final MemberReviewService memberReviewService;
    // 리뷰 작성
    @PostMapping("/create")
    public ResponseEntity<MemberReviewResponse> createReview(
            @PathVariable Long projectPk,
           // @RequestParam Long writerUserPk,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MemberReviewRequest dto) {

        Long writerUserPk = userDetails.getUser().getUserPk();
        MemberReviewResponse responseDTO = memberReviewService.createReview(projectPk, writerUserPk, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/written")
    public ResponseEntity<?> getMyWrittenReviews(
            @PathVariable Long projectPk,
            //@AuthenticationPrincipal CustomUserDetails userDetails
            @RequestParam Long userPk
    ) {
        List<MemberReviewResponse> reviews = memberReviewService.getMyWrittenReviews(userPk, projectPk);
        //List<MemberReviewResponse> reviews = memberReviewService.getMyWrittenReviews(userDetails.getUserPk(), projectPk);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/received")
    public ResponseEntity<?> getMyReceivedReviews(
            @PathVariable Long projectPk,
            //@AuthenticationPrincipal CustomUserDetails userDetails
            @RequestParam Long userPk
    ) {
        List<MemberReviewResponse> reviews = memberReviewService.getMyReceivedReviews(userPk, projectPk);
       // List<MemberReviewResponse> reviews = memberReviewService.getMyReceivedReviews(userDetails.getUserPk(), projectPk);
        return ResponseEntity.ok(reviews);
    }

}