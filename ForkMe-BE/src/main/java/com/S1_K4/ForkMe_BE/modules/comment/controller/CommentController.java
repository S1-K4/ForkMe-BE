package com.S1_K4.ForkMe_BE.modules.comment.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.comment.dto.CommentResponseDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.CreateCommentDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.UpdateCommentDTO;
import com.S1_K4.ForkMe_BE.modules.comment.service.CommentService;
import com.S1_K4.ForkMe_BE.modules.comment.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.controller
 * @fileName : CommentController
 * @date : 2025-08-11
 * @description : 댓글 Controller
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글,대댓글 등록
     */
    @PostMapping("/{profilePk}")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createProjectComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateCommentDTO dto, @PathVariable Long profilePk) {
        Long userPk = userDetails.getUserPk();
        CommentResponseDTO responseDto = commentService.createProjectComment(userPk, profilePk, dto);
        return ResponseEntity.ok(ApiResponse.success(responseDto, "댓글 등록 완료"));
    }

    /**
     * 댓글,대댓글 수정
     */
    @PutMapping("/{commentPk}")
    public ResponseEntity<ApiResponse<UpdateCommentDTO>> updateComment(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody UpdateCommentDTO dto, @PathVariable Long commentPk){
        Long userPk = userDetails.getUserPk();
        return ResponseEntity.ok(ApiResponse.success(commentService.updateComment(userPk, commentPk, dto), "댓글 수정 완료"));
    }

    /**
     * 댓글,대댓글 삭제
     */
    @DeleteMapping("/{commentPk}")
    public ResponseEntity<ApiResponse<Long>> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentPk){
        Long userPk = userDetails.getUserPk();
        commentService.deleteComment(userPk, commentPk);
        return ResponseEntity.ok(ApiResponse.success(commentPk, "댓글 삭제 완료"));
    }


}