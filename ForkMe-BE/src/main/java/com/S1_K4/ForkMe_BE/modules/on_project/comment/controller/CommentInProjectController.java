package com.S1_K4.ForkMe_BE.modules.on_project.comment.controller;

import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.service.CommentInProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/comments")
public class CommentInProjectController {


    private final CommentInProjectService commentInProjectService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreateRequest request) {
        log.info("댓글 생성 요청: boardInProjectPk={}, userPk={}, comment={}",
                request.getBoardInProjectPk(), request.getUserPk(), request.getComment());
        CommentResponse response = commentInProjectService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 댓글 목록 조회 (게시판 기준)
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByBoard(@RequestParam Long boardInProjectPk) {
        List<CommentResponse> comments = commentInProjectService.getCommentsByBoardId(boardInProjectPk);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentInProjectService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}