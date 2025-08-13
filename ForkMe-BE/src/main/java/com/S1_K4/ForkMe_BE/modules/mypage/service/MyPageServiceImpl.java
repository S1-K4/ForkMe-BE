package com.S1_K4.ForkMe_BE.modules.mypage.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.MyApplyListResponseDto;
import com.S1_K4.ForkMe_BE.modules.apply.service.ApplyService;
import com.S1_K4.ForkMe_BE.modules.mypage.dto.MyPageResponseDto;
import com.S1_K4.ForkMe_BE.modules.project.dto.CompletedProjectSummaryDto;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserProfile;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.mypage.service
 * @fileName : MyPageServiceImpl
 * @date : 2025-08-07
 * @description : 마이페이지 서비스
 */
@Slf4j
@Service
@AllArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;

    private final ApplyService ApplyService;
    private final ProjectService ProjectService;

    @Override
    public MyPageResponseDto getMyPage(Long userPk) {

        // 유저 기본 정보
        UserProfile userProfile = userRepository.findByUserPk(userPk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User PK: " + userPk));

        // 유저 기술 스택
        List<TechStackResponseDTO> techStacks = userTechStackRepository.findUserTechStackByUserPk(userPk);
        log.info("techStacks : " + techStacks.toString());

        // 지원 내역
        List<String> stateList = List.of("PENDING");
        List<MyApplyListResponseDto> applyList = ApplyService.getMyApplyList(userPk, stateList);

        // 프로젝트 서머리
        List<CompletedProjectSummaryDto> completedProjectSummaryList = ProjectService.getCompletedProjectSummaryList(userPk);

        return MyPageResponseDto.builder()
                .userProfile(userProfile)
                .techStack(techStacks)
                .applyList(applyList)
                .completedProjectList(completedProjectSummaryList)
                .build();
    }

}