package com.S1_K4.ForkMe_BE.modules.like.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.like.dto.LikeDTO;
import com.S1_K4.ForkMe_BE.modules.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.controller
 * @fileName : LikeController
 * @date : 2025-08-11
 * @description : LikeController
 */
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /**
     * 좋아요 여부 조회
     * */
    @GetMapping("/{profilePk}/me")
    public ResponseEntity<ApiResponse<Boolean>> checkLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long profilePk){
        Long userPk = userDetails.getUserPk();
        boolean hasLiked = likeService.hasUserLikeProfile(userPk, profilePk);
        return ResponseEntity.ok(ApiResponse.success(hasLiked,"좋아요 여부 조회 완료"));

    }
    

    /**
     * profile에 좋아요 추가
     * */
    @PostMapping("/{profilePk}")
    public ResponseEntity<ApiResponse<LikeDTO>> createLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long profilePk){
        Long userPk = userDetails.getUserPk();
        return ResponseEntity.ok(ApiResponse.success(likeService.createLike(userPk, profilePk), "좋아요 추가 성공"));
    }

    /**
     * 좋아요 삭제
     * */
    @DeleteMapping("/{profilePk}")
    public ResponseEntity<ApiResponse<LikeDTO>> deleteLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long profilePk){
        Long userPk = userDetails.getUserPk();
        return ResponseEntity.ok(ApiResponse.success(likeService.deleteLike(userPk, profilePk), "좋아요 취소 성공"));
    }

    @GetMapping("{profilePk}")
    public ResponseEntity<ApiResponse<Long>> countLike(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long profilePk){
        Long userPk = userDetails.getUserPk();
        Long count = likeService.countLike(profilePk);
        return ResponseEntity.ok(ApiResponse.success(count, "좋아요 수 조회 성공"));
    }

 }