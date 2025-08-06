package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectListResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    
}
