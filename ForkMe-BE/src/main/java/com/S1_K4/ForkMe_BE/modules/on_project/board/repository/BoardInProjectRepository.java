package com.S1_K4.ForkMe_BE.modules.on_project.board.repository;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.repository
 * @fileName : BoardInProjectRepository
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 repository 클래스
 */

@Repository
public interface BoardInProjectRepository extends JpaRepository<BoardInProject, Long> {

    // 특정 프로젝트에 속한 게시글 전체 조회
    // List<BoardInProject> findByProject_ProjectPkAndDeletedYN(Long projectPk, Yn deletedYN);
    Page<BoardInProject> findByProject_ProjectPkAndDeletedYNOrderByCreatedAtDesc(Long projectPk, Yn deletedYN, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BoardInProject b SET b.deletedYN = 'Y' WHERE b.project.projectPk IN (:projectPkList)")
    void softDeleteByProjectPkInBulk(List<Long> projectPkList);
}
