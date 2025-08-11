package com.S1_K4.ForkMe_BE.modules.on_project.comment.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<CommentInProject> findByBoardInProject_BoardInProjectPkOrderByCreatedAtAsc(Long boardInProjectPk);
    List<CommentInProject> findByBoardInProject(BoardInProject boardInProject);
}
