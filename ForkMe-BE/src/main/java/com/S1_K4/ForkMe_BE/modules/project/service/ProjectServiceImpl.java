package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectPositionRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.service
 * @fileName : ProjectServiceImpl
 * @date : 2025-08-05
 * @description : ProjectServiceImpl
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectRepository projectRepository;


    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponseDTO getProjectDetail(Long projectPK){
        System.out.println("=============projectPk = "+projectPK +"=================");

        Optional<Project> projectOpt = projectRepository.findWithProfileAndUserByProjectPk(projectPK);
        System.out.println("projectOpt = " + projectOpt);
        Project project = projectOpt.orElseThrow(() -> new RuntimeException("프로젝트 못찾음"));

        System.out.println("project = " + project.getProjectTitle());
        System.out.println("projectProfile = " + project.getProjectProfile()); // 이게 null이면 관계 문제
        System.out.println("user = " + project.getUser().getNickname());

        ProjectProfile profile = project.getProjectProfile();

        List<PositionResponseDTO> positions = projectPositionRepository.findPositionsByProfilePk(profile.getProjectProfilePk());
        List<TechStackResponseDTO> teckStacks = projectTechStackRepository.findTechStacksByProfilePk(profile.getProjectProfilePk());

        return ProjectDetailResponseDTO.builder()
                .projectPk(project.getProjectPk())
                .projectProfilePk(profile.getProjectProfilePk())
                .userPk(project.getUser().getUserPk())
                .projectProfileTitle(profile.getProjectProfileTitle())
                .projectProfileContent(profile.getProjectProfileContent())
                .projectStatus(project.getProjectStatus().getDescription()) //프로젝트 상태
                .progressType(profile.getProgressType().getDescription())   //진행방식
                .positions(positions)   //모집 포지션
                .techStacks(teckStacks) //기술 스택
                .recruitmentStartDate(profile.getRecruitmentStartDate())    //모집 시작일
                .recruitmentEndDate(profile.getRecruitmentEndDate())        //모집 마감일
                .projectStartDate(project.getProjectStartDate())            //프로젝트 시작 일정
                .projectEndDate(project.getProjectEndDate())                //프로젝트 마감일정
                .expectedMembers(profile.getExpectedMembers())                //예상 모집인원
                .build();
    }
}
