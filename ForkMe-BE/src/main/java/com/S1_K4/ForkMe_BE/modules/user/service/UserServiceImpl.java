package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.SideBarProjectDto;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.dto.SidebarResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.entity.UserTechStack;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.StackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final UserTechStackRepository userTechStackRepository;
    private final StackRepository stackRepository;
    private final ProjectRepository projectRepository;

    @Override
    public SidebarResponseDto getSidebarInfo(Long userPk) {

        // 준비중인 프로젝트 -> 모집
        // project_status = RECRUITING
        List<SideBarProjectDto> preparingProject = projectRepository.findRecruitingProjectsByUser(userPk);

        // 워크스페이스 -> 진행중, 충원
        // project_status = IN_PROGRESS, ADDING
        List<SideBarProjectDto> workSpace = projectRepository.findProgressProjectsByUser(userPk);


        return SidebarResponseDto.builder()
                .preparingProjectList(preparingProject)
                .workSpaceList(workSpace)
                .build();
    }


    //@Override
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


}