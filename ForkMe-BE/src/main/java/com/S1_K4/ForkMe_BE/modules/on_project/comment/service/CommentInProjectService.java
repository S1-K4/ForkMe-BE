package com.S1_K4.ForkMe_BE.modules.on_project.comment.service;

import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.dto.CommentUpdateRequest;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.service
 * @fileName : CommentInProjectService
 * @date : 2025-08-10
 * @description :
 */
public interface CommentInProjectService {

    CommentResponse createComment(CommentCreateRequest request);

    List<CommentResponse> getCommentsByBoardId(Long boardPk);

    CommentResponse updateComment(Long commentInProjectPk, CommentUpdateRequest request);

    void deleteComment(Long commentPk);
}
