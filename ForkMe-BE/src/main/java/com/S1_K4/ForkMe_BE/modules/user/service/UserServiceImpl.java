package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.PreparingProjectDto;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.user.dto.SidebarResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.entity.UserTechStack;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.TechStackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.service
 * @fileName : UserServiceImpl
 * @date : 2025-08-06
 * @description : 유저 서비스
 */

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserTechStackRepository userTechStackRepository;
    private final TechStackRepository techStackRepository;

    @Override
    public SidebarResponseDto getSidebarInfo(User user) {
        // 준비중인 프로젝트
        List<PreparingProjectDto> preparingProject = null;
        // 워크스페이스
        List<PreparingProjectDto> workSpace = null;

        // 테스트용 만들기
        String[] projectNameArr = {"프로젝트 기획하자", "프로프로", "가나다라"};
        ProjectStatus[] projectStatuses = {ProjectStatus.PLANNING, ProjectStatus.RECRUITING, ProjectStatus.RECRUITING};
        preparingProject = new ArrayList<>();
        workSpace = new ArrayList<>();
        for (Long i = 0L; i <3 ; i++) {
            PreparingProjectDto dto = new PreparingProjectDto(i, projectNameArr[i.intValue()], projectStatuses[i.intValue()] );
            preparingProject.add(dto);
            workSpace.add(dto);
        }

        return SidebarResponseDto.builder()
                .preparingProjectList(preparingProject)
                .workSpaceList(workSpace)
                .build();
    }





    //@Override
    @Transactional
    public void updateUserTechStack(User user, List<Long> techStackList) {
        // 기존 기술 스택 삭제
        userTechStackRepository.deleteAllByUser(user);

        // 새로운 기술 스택 저장
        if (techStackList != null && !techStackList.isEmpty()) {
            List<TechStack> techStacks = techStackRepository.findAllById(techStackList);
            List<UserTechStack> userTechStacks = techStacks.stream()
                    .map(stack -> new UserTechStack(user, stack))
                    .toList();
            userTechStackRepository.saveAll(userTechStacks);
        }
    }


}