package com.S1_K4.ForkMe_BE.modules.apply.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.service.ApplyService;
import com.S1_K4.ForkMe_BE.modules.auth.dto.CustomUserDetails;
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
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ApplyController {
    private final ApplyService applyService;

    /**
     * 생성폼 조회(기술스택, 모집분야 SELECTBOX)
     */
    @GetMapping("/{projectPk}/applies/form")
    public ResponseEntity<ApiResponse<ApplyCreateFormDTO>> getApplyCreateFrom(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long projectPk) {
        Long userPk = userDetails.getUserPk();
        ApplyCreateFormDTO dto = applyService.getApplyCreateForm(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto, "신청서 생성 폼 데이터 조회 성공"));
    }

    /**
     * 신청서 작성
     */
    @PostMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> createApply(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ApplyCreateRequestDTO dto,
            @PathVariable("projectPk") Long projectPk
    ) {
        Long userPk = userDetails.getUserPk();
        ApplyResponseDTO saved = applyService.createApply(userPk, projectPk, dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "신청서 작성 완료"));
    }

    /**
     * 신청서 단건 조회(사용자 및 팀장 모두 가능)
     */
    @GetMapping("/{projectPk}/applies/{applyPk}")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> getApply(
            @PathVariable Long projectPk,
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
    @GetMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<List<ApplyListResponseDTO>>> getProjectApplies(
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
    @PostMapping("/{projectPk}/applies/{applyPk}/cancel")
    public ResponseEntity<ApiResponse<Long>> cancelApply(
            @PathVariable Long projectPk,
            @PathVariable Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userPk = 1L;
        applyService.cancelApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 취소 완료"));
    }

    /**
     * 신청서 수락(팀장)
     */
    @PostMapping("/{projectPk}/applies/{applyPk}/approve")
    public ResponseEntity<ApiResponse<Long>> approveApply(
            @PathVariable("projectPk") Long projectPk,
            @PathVariable("applyPk") Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userPk = 4L;
        applyService.approveApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 수락 완료"));
    }

    /**
     * 신청서 거절(팀장)
     */
    @PostMapping("/{projectPk}/applies/{applyPk}/reject")
    public ResponseEntity<ApiResponse<Long>> rejectApply(
            @PathVariable Long projectPk,
            @PathVariable Long applyPk,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userPk = userDetails.getUserPk();
        applyService.rejectedApply(userPk, projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(applyPk, "신청서 거절 완료"));
    }
}
