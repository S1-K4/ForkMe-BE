package com.S1_K4.ForkMe_BE.modules.on_project.comment.repository;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.repository
 * @fileName : CommentInProjectRepository
 * @date : 2025-08-10
 * @description : 워크스페이스 내 게시판 댓글 repository
 */

@Repository
public interface CommentInProjectRepository extends JpaRepository<CommentInProject, Long> {
    List<CommentInProject> findByBoardInProject_BoardInProjectPkAndDeletedYNOrderByCreatedAtAsc(Long boardInProjectPk, Yn deletedYN);
    List<CommentInProject> findByBoardInProject(BoardInProject boardInProject);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CommentInProject c WHERE c.boardInProject.project.projectPk IN (:projectPkList)")
    void deleteByProjectPkInBulk(List<Long> projectPkList);
}
