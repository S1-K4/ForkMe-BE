package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.*;
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
    //신청서 생성폼 조회
    ApplyCreateFormDTO getApplyCreateForm(Long profilePk);
    
     //신청서 작성 메서드
    ApplyResponseDTO createApply(Long userPk, Long projectPk, ApplyCreateRequestDTO dto);


    //신청서 단건조회 메서드
    ApplyResponseDTO getApply(Long userPk, Long projectPk, Long applyPk);

    //신청서 목록 조회 메서드(팀장)
    List<ApplyListResponseDTO> getProjectApplies(Long userPk, Long projectPk);

    //신청서 취소 메서드
    void cancelApply(Long userPk, Long projectPk, Long applyPk);

    //신청서 수락 메서드(팀장만 가능)
    void approveApply(Long userPk, Long projectPk, Long applyPk);

    //신청서 거절 메서드(팀장만 가능)
    void rejectedApply(Long userPk, Long projectPk, Long applyPk);


    //지원서 리스트
    List<MyApplyListResponseDto> getMyApplyList(Long userPk, List<String> stateList);
}
