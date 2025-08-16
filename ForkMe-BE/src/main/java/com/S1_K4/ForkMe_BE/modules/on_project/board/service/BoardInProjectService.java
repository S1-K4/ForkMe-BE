package com.S1_K4.ForkMe_BE.modules.on_project.board.service;

import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.InBoardCreateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.InBoardDetailResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.InBoardSimpleResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.board.dto.InBoardUpdateRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.board.service
 * @fileName : BoardInProjectService
 * @date : 2025-08-08
 * @description : 워크스페이스 내 게시판 관련 service 클래스
 */
public interface BoardInProjectService {

    BoardInProject createBoard(Long projectPk, Long userPk, InBoardCreateRequest request);
    //게시글 불러오기
    List<InBoardSimpleResponse> getAllBoardsInProject(Long projectPk);
    //상세보기
    InBoardDetailResponse getBoardDetail(Long boardInProjectPk);

    // 게시글 수정
    BoardInProject updateBoard(Long projectPk, Long boardInProjectPk, InBoardUpdateRequest request,
                               List<MultipartFile> newImages, List<MultipartFile> newFiles, Long userPk);

    // 게시글 삭제
    void deleteBoard(Long projectPk, Long boardInProjectPk, Long userPk);


    // 게시글 파일삭제
    void removeFileFromBoard(Long boardPk, String key);

    // 작성자 확인
    Long getAuthorUserPk(Long boardInProjectPk);
}
