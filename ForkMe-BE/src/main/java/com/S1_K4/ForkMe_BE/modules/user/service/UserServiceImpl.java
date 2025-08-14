package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.auth.repository.AuthRepository;
import com.S1_K4.ForkMe_BE.modules.like.repository.LikeRepository;
import com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.service.ProjectService;
import com.S1_K4.ForkMe_BE.modules.user.dto.SideBarResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserInfoResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserProfile;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.entity.UserTechStack;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.StackRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.service
 * @fileName : UserServiceImpl
 * @date : 2025-08-06
 * @description : 유저 서비스
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final ApplyRepository applyRepository;
    private final ApplyTechStackRepository applyTechStackRepository;
    private final AuthRepository authRepository;
    private final LikeRepository likeRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final StackRepository stackRepository;
    private final UserRepository userRepository;
    private final UserTechStackRepository userTechStackRepository;

    @Override
    public UserInfoResponseDto getMyProfile(Long userPk) {

        UserProfile userProfile = userRepository.findByUserPk(userPk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User PK: " + userPk));
        System.out.println("userProfile = " + userProfile.toString());

        SideBarResponseDto sideBarResponseDto = getSidebarInfo(userPk);
        System.out.println("sideBarResponseDto = " + sideBarResponseDto.toString());

        return UserInfoResponseDto.builder()
                .userPk(userProfile.getUserPk())
                .email(userProfile.getEmail())
                .nickname(userProfile.getNickname())
                .profileUrl(userProfile.getProfileUrl())
                .sideBarResponseDto(sideBarResponseDto)
                .build();
    }

    @Override
    public UserProfile getUserProfile(Long userPk) {
        UserProfile userProfile = userRepository.findByUserPk(userPk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User PK: " + userPk));
        return userProfile;
    }


    @Override
    public SideBarResponseDto getSidebarInfo(Long userPk) {

        // 준비중인 프로젝트 -> 모집
        // project_status = RECRUITING
        List<SideBarProjectDto> preparingProject = projectRepository.findRecruitingProjectsByUser(userPk);

        // 워크스페이스 -> 진행중, 충원
        // project_status = IN_PROGRESS, ADDING
        List<SideBarProjectDto> workSpace = projectRepository.findProgressProjectsByUser(userPk);


        return SideBarResponseDto.builder()
                .preparingProjectList(preparingProject)
                .workSpaceList(workSpace)
                .build();
    }


    @Override
    @Transactional
    public void updateUserTechStack(User user, List<Long> techStackList) {
        log.info("updateUserTechStack : " + user.getUserPk() + " / " + techStackList);
        // 기존 기술 스택 삭제
        userTechStackRepository.deleteAllByUser(user);
        log.info("기존 기술 스택 삭제");

        // 새로운 기술 스택 저장
        if (techStackList != null && !techStackList.isEmpty()) {
            List<TechStack> techStacks = stackRepository.findAllById(techStackList);
            List<UserTechStack> userTechStacks = techStacks.stream()
                    .map(stack -> new UserTechStack(user, stack))
                    .toList();
            userTechStackRepository.saveAll(userTechStacks);
        }
    }

    @Override
    @Transactional
    public void withdrawUser(Long userPk) {
        log.info("withdrawUser : " + userPk);

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User PK: " + userPk));
        log.info("user = " + user.toString());

        // 유저 정보 탈퇴로 변경
        user.withdraw();

        // 유저 기술 스택 삭제
        userTechStackRepository.deleteAllByUser(user);
        log.info("유저 기술 스택 삭제");

        // Auth 삭제
        authRepository.deleteAllByUser(user);
        log.info("Auth 삭제");

        // 프로젝트(탈퇴한 유저가 팀장인 프로젝트)
        projectService.withdrawUser(user);

/*
데이터 없어서 주석해놈
        // like
        likeRepository.deleteByUser(user);
        // apply + applyTechStack
        applyTechStackRepository.deleteApplyTechStackByApply_UserInBulk(user);
        applyRepository.deleteApplyByUserInBulk(user);
        // 참여중인 프로젝트
        projectMemberRepository.deleteByUserInBulk(user);
*/

        // 나머지 기능들에서 삭제하거나 deleted_yn 변경하는 과정 필요


        userRepository.save(user);
        log.info("withdrawUser - success");
    }

}