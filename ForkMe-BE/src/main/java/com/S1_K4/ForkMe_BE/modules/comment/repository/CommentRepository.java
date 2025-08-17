package com.S1_K4.ForkMe_BE.modules.comment.repository;

import com.S1_K4.ForkMe_BE.modules.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.comment.repository
 * @fileName : CommentRepository
 * @date : 2025-08-11
 * @description : 댓글 Repository
 */

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //특정 프로젝트 프로필에 달린 댓글 전부 조회
    List<Comment> findByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.deletedYN='Y' WHERE c.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void softDeleteByProjectProfilePkInBulk(List<Long> projectProfilePkList);
}
