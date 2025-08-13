package com.S1_K4.ForkMe_BE.modules.project.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import com.S1_K4.ForkMe_BE.modules.project.dto.*;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /**
    * 프로젝트 상세 정보 조회
    * */
    @GetMapping("/{projectPk}")
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> getProjectDetail(@PathVariable Long projectPk){
        ProjectDetailResponseDTO dto = projectService.getProjectDetail(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto,"프로젝트 상세 정보 조회 성공"));
    }

    /**
     * 프로젝트 목록 조회
     * */
    @GetMapping()
    private ResponseEntity<ApiResponse<Page<ProjectListResponseDTO>>> getProjects(
            @PageableDefault(size= 20, sort = "projectPk", direction = Sort.Direction.DESC)Pageable pageable){
        Page<ProjectListResponseDTO> response = projectService.getProjectList(pageable);
        return ResponseEntity.ok(ApiResponse.success(response,"프로젝트 목록 조회 성공"));
    }

    /**
     * 프로젝트 생성폼 조회
     * */
    @GetMapping("/form-info")
    public ResponseEntity<ApiResponse<ProjectCreateFormDTO>> getCreateFormInfo(@AuthenticationPrincipal CustomUserDetails userDetails, ProjectCreateFormDTO dto){
        Long userPk = userDetails.getUserPk();
        ProjectCreateFormDTO formInfo = projectService.getProjectCreateFormInfo(userPk);
        return ResponseEntity.ok(ApiResponse.success(formInfo,"프로젝트 생성 폼 조회 완료"));
    }

    /**
     * 프로젝트 생성
     */
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<String>> createProject(
            @RequestPart("dto") ProjectCreateRequestDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userPk = userDetails.getUserPk();
        Long projectPk = projectService.createdProject(dto, images, userPk);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 생성 완료"));

    }

    /**
     * 프로젝트 삭제(softdelete + 연관된 객체들도 삭제)
     * */
    @DeleteMapping("/{projectPk}")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable Long projectPk, @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userPk = userDetails.getUserPk();
        projectService.deleteProject(projectPk, userPk);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 번호 : "+ projectPk, "프로젝트 삭제 성공"));
    }

    /**
    * 프로젝트 수정폼 호출
    * */
    @GetMapping("/{projectPk}/update-form")
    public ResponseEntity<ApiResponse<ProjectUpdateFormDTO>> getProjectUpdateForm(@PathVariable Long projectPk, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userPk = userDetails.getUserPk();
        return ResponseEntity.ok(ApiResponse.success(projectService.getProjectUpdateForm(projectPk, userPk), "프로젝트 수정폼 호출 성공"));
    }

    /**
     * 프로젝트 수정
     */
    @PutMapping(
            value ="/{projectPk}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(
            @PathVariable Long projectPk,
            @RequestPart ProjectUpdateFormDTO dto,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long userPk = userDetails.getUserPk();
        ProjectResponseDTO responseDTO = projectService.updatedProject(projectPk, dto,newImages, userPk);
        return ResponseEntity.ok(ApiResponse.success(responseDTO,"프로젝트 수정 완료"));

    }


    /**
    * 신청서 상태 조회(기획 -> 모집)
    * */
    @PostMapping("/{projectPk}/status/recruiting")
    public ResponseEntity<ApiResponse<String>> recruiting(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = 1L;
        projectService.toRecruiting(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success("RECRUITING", "상태를 모집으로 변경"));
    }

    /**
     * 신청서 상태 조회(모집 -> 진행)
     * */
    @PostMapping("/{projectPk}/status/progress")
    public ResponseEntity<ApiResponse<String>> progress(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = 1L;
        projectService.toInProgress(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success("IN_PROGRESS", "상태를 진행중으로 변경"));
    }

    /**
     * 신청서 상태 조회(진행 -> 충원)
     * */
    @PostMapping("/{projectPk}/status/adding")
    public ResponseEntity<ApiResponse<String>> adding(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = 2L;
        projectService.toAdding(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success("ADDING", "상태를 충원으로 변경"));
    }

    /**
     * 신청서 상태 조회(진행 -> 종료)
     * */
    @PostMapping("/{projectPk}/status/complete")
    public ResponseEntity<ApiResponse<String>> complete(
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = 2L;
        projectService.toCompleted(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success("COMPLETED", "상태를 종료로 변경"));
    }
}