package com.S1_K4.ForkMe_BE.modules.apply.controller;

import com.S1_K4.ForkMe_BE.global.exception.ApiResponse;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * */
    @GetMapping("/{projectPk}/apply-form")
    public ResponseEntity<ApiResponse<ApplyCreateFormDTO>> getApplyCreateFrom(@PathVariable Long projectPk){

        ApplyCreateFormDTO dto = applyService.getApplyCreateForm(projectPk);
        return ResponseEntity.ok(ApiResponse.success(dto,"신청서 생성 폼 데이터 조회 성공"));
    }

    /**
     * 신청서 작성
     * */
    @PostMapping("/{projectPk}/applies")
    public ResponseEntity<ApiResponse<ApplyResponseDTO>> createApply(
            @RequestBody ApplyCreateRequestDTO dto,
            @PathVariable Long projectPk
    ) {
        Long userPk = 3L;
        ApplyResponseDTO saved = applyService.createApply(userPk, projectPk, dto);
        return ResponseEntity.ok(ApiResponse.success(saved, "신청서 작성 완료"));
    }
}
