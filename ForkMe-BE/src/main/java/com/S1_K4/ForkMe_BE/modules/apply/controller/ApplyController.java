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
    @GetMapping("/{projectPk}/apply-form")
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
            @PathVariable Long projectPk
    ) {
        Long userPk = userDetails.getUserPk();
        Long userPk2 = 2L;
        ApplyResponseDTO saved = applyService.createApply(userPk2, projectPk, dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "신청서 작성 완료"));
    }

    /**
     * 신청서 단건 조회
     */
    @GetMapping("/{projectPk}/apply/{applyPk}")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> getApply(
            @PathVariable Long projectPk,
            @PathVariable Long applyPk
    ) {
        ApplyResponseDTO dto = applyService.getApply(projectPk, applyPk);
        return ResponseEntity.ok(ApiResponse.success(dto, "신청서 단건 조회 성공"));
    }

    /**
     * 내가 작성한 신청서 조회(내 신청 내역)
     */
//    @GetMapping("/{projectPk}/applies")
//    public ResponseEntity<ApiResponse<VOID>> getApplies(){}



    /**
     * 신청서 목록 조회(팀장)
     */
    @GetMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<List<ApplyListResponseDTO>>> getProjectApplies(
//            @PathVariable Long userPk,
            @PathVariable Long projectPk
//            @AuthenticationPrincipal CustomUserDetails user
            ) {

        Long userPk = 3L;

        var applies = applyService.getProjectApplies(userPk, projectPk);
        return ResponseEntity.ok(ApiResponse.success(applies, "신청서 목록 조회 성공"));
    }



}
