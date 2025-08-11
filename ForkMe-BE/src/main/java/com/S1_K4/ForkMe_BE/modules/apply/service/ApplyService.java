package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.service
 * @fileName : ApplyService
 * @date : 2025-08-11
 * @description : ApplyService
 */
public interface ApplyService {
    ApplyCreateFormDTO getApplyCreateForm(Long profilePk);

    /*
     * 신청서 작성 메서드
     * */
    ApplyResponseDTO createApply(Long userPk, Long projectPk, ApplyCreateRequestDTO dto);
}
