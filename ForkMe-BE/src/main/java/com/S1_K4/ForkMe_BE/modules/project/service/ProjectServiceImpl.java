package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.global.common.Yn;
import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.global.common.s3.S3Service;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.apply.Repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.Repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.like.repository.LikeRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.modules.project.dto.*;
import com.S1_K4.ForkMe_BE.modules.project.entity.*;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.project.repository.*;
import com.S1_K4.ForkMe_BE.modules.s3.Entity.S3Image;
import com.S1_K4.ForkMe_BE.modules.s3.Repository.S3Repository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import com.S1_K4.ForkMe_BE.reference.position.repository.PositionRepsitory;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final StackRepository stackRepository;
    private final PositionRepsitory positionRepository;
    private final UserRepository userRepository;
    private final ProjectProfileRepository projectProfileRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final LikeRepository likeRepository;
    private final ApplyRepository applyRepository;
    private final ApplyTechStackRepository applyTechStackRepository;
    private final S3Service s3Service;
    private final S3Repository s3Repository;

    /*
     * 프로젝트 상세 조회
     * */
    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponseDTO getProjectDetail(Long projectPK){
        System.out.println("=============projectPk = "+projectPK +"=================");

        Optional<Project> projectOpt = projectRepository.findWithProfileAndUserByProjectPk(projectPK);
        System.out.println("projectOpt = " + projectOpt);
        Project project = projectOpt.orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        ProjectProfile profile = project.getProjectProfile();

        //댓글 조회
        List<ProjectDetailResponseDTO.CommentDTO> commentDTOList = project.getProjectProfile().getComments().stream()
                        .filter(comment ->  Yn.N.equals(comment.getDeletedYN()))
                                .map(ProjectDetailResponseDTO.CommentDTO::toDTO)
                                        .toList();

        //좋아요 수 조회
        Long likeCount = likeRepository.countByProjectProfile_ProjectProfilePk(profile.getProjectProfilePk());
        
        //포지션, 기술스택 조회
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
                .positions(positions)                                       //모집 포지션
                .techStacks(teckStacks)                                     //기술 스택
                .recruitmentStartDate(profile.getRecruitmentStartDate())    //모집 시작일
                .recruitmentEndDate(profile.getRecruitmentEndDate())        //모집 마감일
                .projectStartDate(project.getProjectStartDate())            //프로젝트 시작 일정
                .projectEndDate(project.getProjectEndDate())                //프로젝트 마감일정
                .expectedMembers(profile.getExpectedMembers())              //예상 모집인원
                .comments(commentDTOList)
                .likeCount(likeCount)
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

    /*
     * 프로젝트 생성폼
     * */
    @Transactional(readOnly = true)
    @Override
    public ProjectCreateFormDTO getProjectCreateFormInfo(){
        //1. 기술 스택 조회
        List<TechStackResponseDTO> techStacks = stackRepository.findAll().stream()
                .map(t -> new TechStackResponseDTO(t.getTechPk(), t.getTechName()))
                .toList();

        //2. 모집분야 조회
        List<PositionResponseDTO> positions = positionRepository.findAll().stream()
                .map(p -> new PositionResponseDTO(p.getPositionPk(), p.getPositionName()))
                .toList();

        //3. 예상 인원(1~10)
        List<Integer> expectedMembers = IntStream.rangeClosed(1,10)
                .boxed()
                .toList();

        //4. 진행방식(enum -> dto)
        List<ProgressEnumDTO> progressTypes = Arrays.stream(ProgressType.values())
                .map(p -> new ProgressEnumDTO(p.name(), p.getDescription()))
                .toList();

        //최종 dto변환
        return ProjectCreateFormDTO.builder()
                .techStacks(techStacks)
                .positions(positions)
                .expected_members(expectedMembers)
                .progressType(progressTypes)
                .build();
    }

    /*
     * 프로젝트 생성(생성 순서 : 프로젝트 -> 프로젝트 프로필 -> 이미지 ->프로젝트 모집인원 -> 프로젝트 기술스택 -> 프로젝트 포지션 )
     * */
    @Override
    @Transactional
    public Long createdProject(ProjectCreateRequestDTO dto, List<MultipartFile> images) {

        User user = userRepository.findById(1L).orElseThrow(()-> new IllegalArgumentException("유저없삼"));

        //프로젝트 생성
        Project project = dto.toProjectEntity(user);
        projectRepository.save(project);

        //프로젝트 프로필 생성
        ProjectProfile projectProfile = dto.toProjectProfileEntity(project);
        projectProfileRepository.save(projectProfile);
        //이미지가 존재하면 이미지 업로드 및 이미지 엔티티 저장
        if (images != null && !images.isEmpty()) {
            List<String> s3Urls = s3Service.uploadFile(images,"images"); //전체 URL 리스트 반환

            for(int i = 0; i<s3Urls.size(); i++){
                MultipartFile file = images.get(i);
                String s3Url = s3Urls.get(i);

                S3Image image = S3Image.builder()
                        .url(s3Url)
                        .projectProfile(projectProfile)
                        .build();

                projectProfile.getImages().add(image);
                s3Repository.save(image);
            }
        }

        //팀장 참여 정보 생성
        ProjectMember projectMember = dto.toLeaderEntity(project, user);
        projectMemberRepository.save(projectMember);

        //기술 스택 연결
        List<TechStack> techStacks = stackRepository.findAllById(dto.getTechPk());
        List<ProjectTechStack> projectStacks = dto.toProjectTechStackEntities(projectProfile, techStacks);
        projectTechStackRepository.saveAll(projectStacks);

        //모집 포지션 연결
        List<Position> positions = positionRepository.findAllById(dto.getPositionPk());
        List<ProjectPosition> projectPositions = dto.toProjectPositionEntities(projectProfile, positions);
        projectPositionRepository.saveAll(projectPositions);

        return project.getProjectPk();
    }

    /*
     * 프로젝트 삭제
     * */
    @Override
    @Transactional
    public void deleteProject(Long projectPk){
      //프로젝트 조회
      Project project = projectRepository.findById(projectPk)
              .orElseThrow(()-> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

      //삭제 여부 확인
      if("N".equals(project.getDeletedYN())){
          throw new CustomException(CustomException.ErrorCode.PROJECT_ALLREDAY_DELETE);
      }

//      //작성자와 로그인한 사용자 일치 여부
//        if(project.getUser().getUserPk().equals("userid")){
//            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
//        }

        //project, projectProfile, comment soft delete
        project.markDeleted();
        ProjectProfile projectProfile = project.getProjectProfile();
        projectProfile.markDeleted();
        for(Comment comment : projectProfile.getComments()){
            comment.markDeleted();
        }
        
        //hard Delete : 워크스페이스 관련 엔티티는 추후 삭제 추가예정(board_in_project,comment_in_project,github_timeline,s3_file,chatting_room,chatting_message,chatting_participant)
        s3Repository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());                 //s3이미지
        likeRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());               //좋아요
        projectTechStackRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());   //프로젝트 기술스택  
        projectPositionRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());    //프로젝트 모집분야
        projectMemberRepository.deleteByProject_ProjectPk(projectPk);                                               //프로젝트 인원
        applyTechStackRepository.deleteByApply_Project_ProjectPk(projectPk);                                        //신청서 기술 스택
        applyRepository.deleteByProject_ProjectPk(projectPk);                                                       //신청서
    }
}