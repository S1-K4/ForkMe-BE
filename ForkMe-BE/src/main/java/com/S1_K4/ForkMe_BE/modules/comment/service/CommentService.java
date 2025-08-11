package com.S1_K4.ForkMe_BE.modules.comment.service;

import com.S1_K4.ForkMe_BE.modules.comment.dto.CommentResponseDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.CreateCommentDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.UpdateCommentDTO;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.service
 * @fileName : CommentService
 * @date : 2025-08-11
 * @description : CommentService
 */
public interface CommentService {
    CommentResponseDTO createProjectComment(Long userPk, Long projectProfilePk, CreateCommentDTO dto);
    UpdateCommentDTO updateComment(Long userPk, Long commentPk, UpdateCommentDTO dto);
    void deleteComment(Long userPk, Long commentPk);
}
