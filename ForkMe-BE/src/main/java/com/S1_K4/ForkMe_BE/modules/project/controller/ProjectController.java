package com.S1_K4.ForkMe_BE.modules.project.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.controller
 * @fileName : ProjectController
 * @date : 2025-08-05
 * @description : Project Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{projectPk}")
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> getProjectDetail(@PathVariable Long projectPk){
        ProjectDetailResponseDTO dto = projectService.getProjectDetail(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto,"프로젝트 상세 정보 조회 성공"));
    }
    @GetMapping()
    private ResponseEntity<ApiResponse<Page<ProjectListResponseDTO>>> getProjects(
            @PageableDefault(size= 20, sort = "projectPk", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ProjectListResponseDTO> response = projectService.getProjectList(pageable);
        return ResponseEntity.ok(ApiResponse.success(response,"성공"));
    }
}
