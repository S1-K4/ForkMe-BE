package com.S1_K4.ForkMe_BE.modules.mypage.dto;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto;
import com.S1_K4.ForkMe_BE.modules.project.dto.CompletedProjectSummaryDto;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.mypage.dto
 * @fileName : MyPageResponseDto
 * @date : 2025-08-06
 * @description : 마이페이지 정보 반환하는 정보 담은 dto
 */

@AllArgsConstructor
@Getter
@Builder
public class MyPageResponseDto {

    // 마이페이지 기본 정보
    private User user;

    // 기술 스택
    private List<TechStackResponseDTO> techStack;

    // 신청 내역
    private List<ApplyDto> applyList;

    // 완료한 프로제트
    private List<CompletedProjectSummaryDto> completedProjectList;

}