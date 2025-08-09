package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.service
 * @fileName : ProjectService
 * @date : 2025-08-05
 * @description : ProjectService
 */
public interface ProjectService {

    //프로젝트 상세 보기
    ProjectDetailResponseDTO getProjectDetail(Long projectPk);

    /*
     * 프로젝트 목록 조회
     * */
    Page<ProjectListResponseDTO> getProjectList(Pageable pageable);

    /*
     * 프로젝트 생성폼(selectbox 옵션)
     * */
    @Transactional(readOnly = true)
    ProjectCreateFormDTO getProjectCreateFormInfo();

    /*
     * 프로젝트 생성(생성 순서 : 프로젝트 -> 프로젝트 프로필 -> 이미지 ->프로젝트 모집인원 -> 프로젝트 기술스택 -> 프로젝트 포지션 )
     * */
    @Transactional
    Long createdProject(ProjectCreateRequestDTO dto, List<MultipartFile> images);

    /*
     * 프로젝트 삭제
     * */
    void deleteProject(Long projectPk);


    /*
     * 프로젝트 수정폼
     * */
    @Transactional(readOnly = true)
    ProjectUpdateFormDTO getProjectUpdateForm(Long projectPk);

    /*
     * 프로젝트 수정
     */
    @Transactional
    ProjectResponseDTO updatedProject(Long projectPk, ProjectUpdateFormDTO dto, List<MultipartFile> newImages);
}