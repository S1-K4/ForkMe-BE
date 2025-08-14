package com.S1_K4.ForkMe_BE.modules.on_project.board.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3Image;
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
 * @fileName : BoardImageRepository
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 이미지 Repository
 */

@Repository
public interface BoardImageRepository extends JpaRepository<S3Image, Long> {



    @Modifying
    @Transactional
    @Query("DELETE FROM S3Image b WHERE b.url IN :urls")
    void deleteByUrls(@Param("urls") List<String> urls);


    List<S3Image> findByBoardInProject(BoardInProject board);

}
