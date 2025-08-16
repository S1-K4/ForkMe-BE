package com.S1_K4.ForkMe_BE.modules.on_project.comment.controller;

import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentUpdateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.service.CommentInProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.controller
 * @fileName : CommentInProjectController
 * @date : 2025-08-10
 * @description : 워크스페이스 내 댓글 컨트롤러
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/on-project/comments")
public class CommentInProjectController {


    private final CommentInProjectService commentInProjectService;

    // 댓글 생성
    @PostMapping("/{boardInProjectPk}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long boardInProjectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CommentCreateRequest request) {

        // 현재 로그인 유저 pk
        Long userPk = userDetails.getUserPk();

        log.info("댓글 생성 요청: boardInProjectPk={}, userPk={}, comment={}",
                boardInProjectPk,userPk, request.getComment());
        CommentResponse response = commentInProjectService.createComment(request, userPk, boardInProjectPk);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 목록 조회 (게시판 기준)
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByBoard(@RequestParam Long boardInProjectPk) {
        List<CommentResponse> comments = commentInProjectService.getCommentsByBoardId(boardInProjectPk);
        return ResponseEntity.ok(comments);
    }

    //댓글 수정
    @PutMapping("/{commentInProjectPk}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentInProjectPk,
                                                          @RequestBody CommentUpdateRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails){
        log.info("댓글 수정 요청: boardInProjectPk={}, userPk={}, comment={}",
                commentInProjectPk, request.getComment());

        // 현재 로그인한 유저 pk
        Long loggedInUserPk = userDetails.getUserPk();


        CommentResponse updated  = commentInProjectService.updateComment(request, loggedInUserPk, commentInProjectPk);

        return ResponseEntity.ok(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentInProjectPk}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentInProjectPk,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 현재 로그인 유저
        Long loggedInUserPk = userDetails.getUserPk();
        commentInProjectService.deleteComment(commentInProjectPk, loggedInUserPk);
        return ResponseEntity.noContent().build();
    }

}