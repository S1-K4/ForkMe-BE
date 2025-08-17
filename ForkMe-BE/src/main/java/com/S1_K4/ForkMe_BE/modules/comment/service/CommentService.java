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
    //댓글 작성
    CommentResponseDTO createProjectComment(Long userPk, Long projectProfilePk, CreateCommentDTO dto);
    
    //댓글 수정
    UpdateCommentDTO updateComment(Long userPk, Long commentPk, UpdateCommentDTO dto);
    
    //댓글 삭제
    void deleteComment(Long userPk, Long commentPk);
}
