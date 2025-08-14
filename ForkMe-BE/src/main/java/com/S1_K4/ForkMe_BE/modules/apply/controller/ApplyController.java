package com.S1_K4.ForkMe_BE.modules.apply.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.service.ApplyService;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.controller
 * @fileName : ApplyController
 * @date : 2025-08-11
 * @description : ApplyController
 */

@Tag(name="신청서", description = "신청서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ApplyController {
    private final ApplyService applyService;

    /**
     * 생성폼 조회(기술스택, 모집분야 SELECTBOX)
     */
    @Operation(summary = "신청서 생성폼 조회",description = "사용자에게 보여줄 신청서 생성폼을 조회합니다. 기술 스택과 모집 분야를 전체 조회 합니다.")
    @GetMapping("/{projectPk}/applies/form")
    public ResponseEntity<ApiResponse<ApplyCreateFormDTO>> getApplyCreateFrom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "신청서를 작성할 프로젝트의 PK", example = "1")
            @PathVariable Long projectPk
    ) {
        Long userPk = userDetails.getUserPk();
        ApplyCreateFormDTO dto = applyService.getApplyCreateForm(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto, "신청서 생성 폼 데이터 조회 성공"));
    }

    /**
     * 신청서 작성
     */
    @Operation(summary = "신청서 작성")
    @PostMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> createApply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ApplyCreateRequestDTO dto,
            @Parameter(description = "신청서를 작성할 프로젝트의 PK", example = "1")
            @PathVariable("projectPk") Long projectPk
    ) {
        Long userPk = userDetails.getUserPk();
        ApplyResponseDTO saved = applyService.createApply(userPk, projectPk, dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "신청서 작성 완료"));
    }

    /**
     * 신청서 단건 조회(사용자 및 팀장 모두 가능)
     */
    @Operation(summary = "신청서 단건 조회", description = "신청서 단건 조회 API 입니다. 사용자 및 팀장 모두 조회 가능합니다.")
    @GetMapping("/{projectPk}/applies/{applyPk}")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> getApply(
            @Parameter(description = "신청서를 조회할 프로젝트의 PK", example = "1")
            @PathVariable Long projectPk,
            @Parameter(description = "조회할 신청서 PK", example = "2")
            @PathVariable Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = userDetails.getUserPk();
        ApplyResponseDTO dto = applyService.getApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(dto, "신청서 단건 조회 성공"));
    }

    /**
     * 신청서 목록 조회(팀장)
     */
    @Operation(summary = "신청서 목록 조회", description = "신청서 목록 조회 API 입니다. 해당 프로젝트의 팀장만이 조회 가능합니다.")
    @GetMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<List<ApplyListResponseDTO>>> getProjectApplies(
            @Parameter(description = "신청서 목록을 조회할 프로젝트의 PK", example = "1")
            @PathVariable Long projectPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = userDetails.getUserPk();
        List<ApplyListResponseDTO> applies = applyService.getProjectApplies(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success(applies, "신청서 목록 조회 성공"));
    }

    /**
     * 신청서 취소
     */
    @Operation(summary = "신청서 취소")
    @PostMapping("/{projectPk}/applies/{applyPk}/cancel")
    public ResponseEntity<ApiResponse<Long>> cancelApply(
            @Parameter(description = "취소할 신청서의 프로젝트의 PK", example = "1")
            @PathVariable Long projectPk,
            @Parameter(description = "취소할 신청서 PK", example = "2")
            @PathVariable Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = userDetails.getUserPk();
        applyService.cancelApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 취소 완료"));
    }

    /**
     * 신청서 수락(팀장)
     */
    @Operation(summary = "신청서 수락", description = "신청서 수락 API입니다. 해당 프로젝트의 팀장만이 수락 가능합니다.")
    @PostMapping("/{projectPk}/applies/{applyPk}/approve")
    public ResponseEntity<ApiResponse<Long>> approveApply(
            @Parameter(description = "수락할 신청서의 프로젝트의 PK", example = "1")
            @PathVariable("projectPk") Long projectPk,
            @Parameter(description = "수락할 신청서 PK", example = "2")
            @PathVariable("applyPk") Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = userDetails.getUserPk();
        applyService.approveApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 수락 완료"));
    }

    /**
     * 신청서 거절(팀장)
     */
    @Operation(summary = "신청서 거절", description = "신청서 거절 API입니다. 해당 프로젝트의 팀장만이 거절 가능합니다.")
    @PostMapping("/{projectPk}/applies/{applyPk}/reject")
    public ResponseEntity<ApiResponse<Long>> rejectApply(
            @Parameter(description = "거절할 신청서의 프로젝트의 PK", example = "1")
            @PathVariable Long projectPk,
            @Parameter(description = "거절할 신청서 PK", example = "2")
            @PathVariable Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userPk = userDetails.getUserPk();
        applyService.rejectedApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 거절 완료"));
    }
}
