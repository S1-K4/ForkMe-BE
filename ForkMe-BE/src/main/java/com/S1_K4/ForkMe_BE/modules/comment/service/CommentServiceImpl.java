package com.S1_K4.ForkMe_BE.modules.comment.service;

import com.S1_K4.ForkMe_BE.global.common.cache.CacheNames;
import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.comment.dto.CommentResponseDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.CreateCommentDTO;
import com.S1_K4.ForkMe_BE.modules.comment.dto.UpdateCommentDTO;
import com.S1_K4.ForkMe_BE.modules.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.modules.comment.repository.CommentRepository;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectProfileRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.serviceImpl
 * @fileName : CommentServiceImpl
 * @date : 2025-08-11
 * @description : 댓글 ServiceImpl
 */

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProjectProfileRepository projectProfileRepository;
    private final CacheManager cacheManager;

    /**
     * 댓글 등록 메서드
     */
    @Override
    @Transactional
    public CommentResponseDTO createProjectComment(Long userPk, Long projectProfilePk, CreateCommentDTO dto){
        User user = userRepository.findById(userPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        ProjectProfile profile = projectProfileRepository.findById(projectProfilePk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        Comment parent = null;

        if(dto.getParentPk()!= null){
            parent = commentRepository.findById(dto.getParentPk())
                    .orElseThrow(()->new CustomException(CustomException.ErrorCode.PARENT_COMMENT_NOT_FOUND));

            //상위 댓글이 게시글의 댓글이 아니거나
            //상위 댓글과 생성 댓글의 profilePk가 다르거나
            //상위 댓글이 이미 하위댓글일 때

            if(parent.getProjectProfile() == null || !parent.getProjectProfile().equals(profile) || parent.getParent()!= null){
                throw new CustomException(CustomException.ErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
        }

        Comment comment = Comment.builder()
                .comment(dto.getComment())
                .parent(parent)
                .user(user)
                .projectProfile(profile)
                .build();

        Comment saved = commentRepository.save(comment);

        //커밋 이후 캐시 무효화
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                evictDetailAndList(comment.getProjectProfile().getProject().getProjectPk());
            }
        });
        return CommentResponseDTO.builder()
                .comment(saved.getComment())
                .commentPk(saved.getCommentPk())
                .parentPk(saved.getParent() != null ? saved.getParent().getCommentPk() : null)
                .build();
    }

    /**
     * 댓글 수정 메서드
     */
    @Override
    @Transactional
    public UpdateCommentDTO updateComment(Long userPk, Long commentPk, UpdateCommentDTO dto){
        Comment comment = commentRepository.findById(commentPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.COMMENT_NOT_FOUND));

        //댓글 작성자와 로그인한 유저가 일치하지 않으면
        if(!comment.getUser().getUserPk().equals(userPk)){
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }

        comment.updateComment(dto.getComment());

        //커밋 이후 캐시 무효화
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                evictDetailAndList(comment.getProjectProfile().getProject().getProjectPk());
            }
        });

        return UpdateCommentDTO.builder()
                .comment(comment.getComment())
                .build();
    }

    /**
    * 댓글 삭제 메서드
    * */
    @Override
    @Transactional
    public void deleteComment(Long userPk, Long commentPk){
        Comment comment = commentRepository.findById(commentPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.COMMENT_NOT_FOUND));

        if(comment.getDeletedYN().equals(Yn.Y)){
            throw new CustomException(CustomException.ErrorCode.COMMENT_NOT_FOUND);
        }

        if(!comment.getUser().getUserPk().equals(userPk)){
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }

        comment.markDeleted();

        //커밋 이후 캐시 무효화
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                evictDetailAndList(comment.getProjectProfile().getProject().getProjectPk());
            }
        });
    }

    private void evictDetailAndList(Long projectPk) {
        Cache detail = cacheManager.getCache(CacheNames.PROJECT_DETAIL_STATIC);
        Cache list   = cacheManager.getCache(CacheNames.PROJECT_LIST);

        detail.evictIfPresent(projectPk);   //상세보기 : 해당 projectPk를 가진 캐쉬만 무효화
        list.clear();                       //목록보기 : 모두 무효화
    }


}