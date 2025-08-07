package com.S1_K4.ForkMe_BE.modules.project.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /*
    * 프로젝트 상세 정보 조회
    * */
    @GetMapping("/{projectPk}")
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> getProjectDetail(@PathVariable Long projectPk){
        ProjectDetailResponseDTO dto = projectService.getProjectDetail(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto,"프로젝트 상세 정보 조회 성공"));
    }

    /*
     * 프로젝트 목록 조회
     * */
    @GetMapping()
    private ResponseEntity<ApiResponse<Page<ProjectListResponseDTO>>> getProjects(
            @PageableDefault(size= 20, sort = "projectPk", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ProjectListResponseDTO> response = projectService.getProjectList(pageable);
        return ResponseEntity.ok(ApiResponse.success(response,"프로젝트 목록 조회 성공"));
    }


    /*
     * 프로젝트 삭제(softdelete + 연관된 객체들도 삭제)
     * */
    @DeleteMapping("/{projectPk}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long projectPk) {
        projectService.deleteProject(projectPk);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 삭제 성공"));
    }

    /*
     * 프로젝트 생성폼 조회
     * */
    @GetMapping("/form-info")
    public ResponseEntity<ApiResponse<ProjectCreateFormDTO>> getCreateFormInfo(){
        ProjectCreateFormDTO formInfo = projectService.getProjectCreateFormInfo();
        return ResponseEntity.ok(ApiResponse.success(formInfo,"프로젝트 생성 폼 조회 완료"));
    }

    /**
     * 프로젝트 생성
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<String>> createProject(
            @RequestPart("dto") ProjectCreateRequestDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images){
        Long projectPk = projectService.createdProject(dto, images);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 생성 완료"));

    }
}