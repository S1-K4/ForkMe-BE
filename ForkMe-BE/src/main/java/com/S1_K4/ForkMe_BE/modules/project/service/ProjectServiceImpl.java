package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectDetailResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.dto.ProjectListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectPositionRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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


    /*
    * 프로젝트 상세 조회
    * */
    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponseDTO getProjectDetail(Long projectPK){
        Optional<Project> projectOpt = projectRepository.findWithProfileAndUserByProjectPk(projectPK);
        System.out.println("projectOpt = " + projectOpt);
        Project project = projectOpt.orElseThrow(() -> new RuntimeException("프로젝트 못찾음"));

        ProjectProfile profile = project.getProjectProfile();

        List<PositionResponseDTO> positions = projectPositionRepository.findPositionsByProfilePk(profile.getProjectProfilePk());
        List<TechStackResponseDTO> teckStacks = projectTechStackRepository.findTechStacksByProfilePk(profile.getProjectProfilePk());

        return ProjectDetailResponseDTO.builder()
                .projectPk(project.getProjectPk())
                .projectProfilePk(profile.getProjectProfilePk())
                .userPk(project.getUser().getUserPk())
                .nickname(project.getUser().getNickname())
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


    /*
     * 프로젝트 목록 조회
     * */
    @Override
    public Page<ProjectListResponseDTO> getProjectList(Pageable pageable) {
        Page<Project> projectPage = projectRepository.findProjectsWithUserAndProfile(pageable);

        return projectPage.map(project -> {
            ProjectProfile profile = project.getProjectProfile();

            List<PositionResponseDTO> positions = projectPositionRepository
                    .findPositionsByProfilePk(profile.getProjectProfilePk());

            List<TechStackResponseDTO> techStacks = projectTechStackRepository
                    .findTechStacksByProfilePk(profile.getProjectProfilePk());

            Long projectPk = project.getProjectPk();
            Long projectProfilePk = profile.getProjectProfilePk();
            Long userPk = project.getUser().getUserPk();
            String nickname = project.getUser().getNickname();
            String projectProfileTitle = profile.getProjectProfileTitle();
            String projectStatus = project.getProjectStatus().name();
            LocalDate recruitmentStartDate = profile.getRecruitmentStartDate();
            LocalDate recruitmentEndDate = profile.getRecruitmentEndDate();
            int expectedMembers = profile.getExpectedMembers();

            return ProjectListResponseDTO.builder()
                    .projectPk(projectPk)
                    .projectProfilePk(projectProfilePk)
                    .userPk(userPk)
                    .nickname(nickname)
                    .projectProfileTitle(projectProfileTitle)
                    .projectStatus(projectStatus)
                    .positions(positions)
                    .techStacks(techStacks)
                    .recruitmentStartDate(recruitmentStartDate)
                    .recruitmentEndDate(recruitmentEndDate)
                    .expectedMembers(expectedMembers)
                    .build();
        });
    }
}