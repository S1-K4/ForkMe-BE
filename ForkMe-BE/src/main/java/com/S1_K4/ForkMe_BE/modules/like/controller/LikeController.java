package com.S1_K4.ForkMe_BE.modules.like.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.like.dto.LikeDTO;
import com.S1_K4.ForkMe_BE.modules.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * profile에 좋아요 추가
     * */
    @PostMapping("/{profilePk}")
    public ResponseEntity<ApiResponse<LikeDTO>> createLike(@PathVariable Long profilePk){
        Long userPk = 3L;
        return ResponseEntity.ok(ApiResponse.success(likeService.createLike(userPk, profilePk), "좋아요 추가 성공"));
    }
 }