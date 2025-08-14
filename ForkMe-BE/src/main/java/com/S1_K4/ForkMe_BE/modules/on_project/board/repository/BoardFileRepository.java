package com.S1_K4.ForkMe_BE.modules.on_project.board.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3File;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.repository
 * @fileName : BoardFileRepository
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 첨부파일 repository
 */

@Repository
public interface BoardFileRepository extends JpaRepository<S3File, Long> {
    List<S3File> findByBoardInProject(BoardInProject boardInProject);

    @Modifying
    @Transactional
    @Query("DELETE FROM S3File f WHERE f.url IN :urls")
    void deleteByUrls(@Param("urls") List<String> urls);


}
