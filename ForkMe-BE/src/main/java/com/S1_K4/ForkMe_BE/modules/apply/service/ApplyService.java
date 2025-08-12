package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /*
     * 신청서 단건조회 메서드
     * */
    @Transactional(readOnly = true)
    ApplyResponseDTO getApply(Long userPk, Long projectPk, Long applyPk);

    /*
     * 신청서 목록 조회(팀장기준)
     * */
    @Transactional(readOnly = true)
    List<ApplyListResponseDTO> getProjectApplies(Long userPk, Long projectPk);

    @Transactional
    void cancelApply(Long userPk, Long projectPk, Long applyPk);

    //신청서 수락 메서드(팀장만 가능)
    @Transactional
    void approveApply(Long userPk, Long projectPk, Long applyPk);

    //신청서 거절 메서드(팀장만 가능)
    @Transactional
    void rejectedApply(Long userPk, Long projectPk, Long applyPk);
}
