package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.global.common.cache.CacheNames;
import com.S1_K4.ForkMe_BE.global.common.cache.project.EvictScope;
import com.S1_K4.ForkMe_BE.global.common.cache.project.ProjectCacheEvictEvent;
import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import com.S1_K4.ForkMe_BE.global.common.s3.S3Service;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.apply.service.ApplyService;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.repository.ChattingRoomRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import com.S1_K4.ForkMe_BE.modules.comment.entity.Comment;
import com.S1_K4.ForkMe_BE.modules.comment.repository.CommentRepository;
import com.S1_K4.ForkMe_BE.modules.like.repository.LikeRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardFileRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardImageRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.board.repository.BoardInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.entity.CommentInProject;
import com.S1_K4.ForkMe_BE.modules.on_project.comment.repository.CommentInProjectRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewMypageDto;
import com.S1_K4.ForkMe_BE.modules.on_project.review.repository.MemberReviewRepository;
import com.S1_K4.ForkMe_BE.modules.on_project.schedule.repository.ScheduleRepository;
import com.S1_K4.ForkMe_BE.modules.project.dto.*;
import com.S1_K4.ForkMe_BE.modules.project.entity.*;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProgressType;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.project.repository.*;
import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3File;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3Image;
import com.S1_K4.ForkMe_BE.modules.s3.repository.S3Repository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import com.S1_K4.ForkMe_BE.reference.position.repository.PositionRepository;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.service
 * @fileName : ProjectServiceImpl
 * @date : 2025-08-05
 * @description : ProjectServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectRepository projectRepository;
    private final StackRepository stackRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final ProjectProfileRepository projectProfileRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final LikeRepository likeRepository;
    private final ApplyRepository applyRepository;
    private final ApplyTechStackRepository applyTechStackRepository;
    private final S3Service s3Service;
    private final S3Repository s3Repository;
    private final MemberReviewRepository memberReviewRepository;
    private final CommentRepository commentRepository;
    private final ApplicationEventPublisher publisher;

    private final ChattingRoomRepository chattingRoomRepository;
    private final ChattingService chattingService;
    private final ApplyService applyService;
    private final BoardInProjectRepository boardInProjectRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardImageRepository boardImageRepository;
    private final CommentInProjectRepository commentInProjectRepository;
    private final ScheduleRepository scheduleRepository;

    /*
     * 프로젝트 상세 조회
     * */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.PROJECT_DETAIL_STATIC, key = "#p0", sync = true)
    public ProjectDetailResponseDTO getProjectDetail(Long projectPK){

        Optional<Project> projectOpt = projectRepository.findWithProfileAndUserByProjectPk(projectPK);
        Project project = projectOpt.orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        ProjectProfile profile = project.getProjectProfile();

        //댓글 조회
        List<ProjectDetailResponseDTO.CommentDTO> commentDTOList = project.getProjectProfile().getComments().stream()
                        .filter(comment ->  Yn.N.equals(comment.getDeletedYN()))
                                .map(ProjectDetailResponseDTO.CommentDTO::toDTO)
                                        .toList();

        //좋아요 수 조회
        Long likeCount = likeRepository.countByProjectProfile_ProjectProfilePk(profile.getProjectProfilePk());

        //포지션, 기술스택 조회`
        List<PositionResponseDTO> positions = projectPositionRepository.findPositionsByProfilePk(profile.getProjectProfilePk());
        List<TechStackResponseDTO> teckStacks = projectTechStackRepository.findTechStacksByProfilePk(profile.getProjectProfilePk());

        List<ProjectImageDTO> images = s3Repository.findAllImagesByProfilePk(profile.getProjectProfilePk());

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
                .images(images)
                .build();
    }

    /*
     * 프로젝트 목록 조회
     * */
    @Override
    @Cacheable(
            cacheNames = CacheNames.PROJECT_LIST,
            keyGenerator = "projectListKeyGenerator",
            unless = "#result == null || #result.content == null || #result.content.isEmpty()"      //결과가 NULL이거나 비어있으면 캐싱x
    )
    @Transactional(readOnly = true)
    public PageResponse<ProjectListResponseDTO> getProjectList(Pageable pageable) {
        Page<Project> projectPage = projectRepository.findProjectsWithUserAndProfile(pageable);

        /**현재 페이지에 포함된 profilePk들만 추출 -> 연관된 컬렉션(포지션/기술스택)을 벌크로 가져오기 위함*/
        List<Long> profilePks = projectPage.getContent().stream()   //현재 페이지의 엔티티 리스트를 스트림으로 순회
                .map(p -> p.getProjectProfile().getProjectProfilePk()) //각 Project가 가진 ProjectProfile의 PK만 추출
                .toList();  //리스트로 변환

        //만약 페이지가 비어있다면(데이터가 없다면) 빈 pageResopnse반환
        if (projectPage.isEmpty()) {
            Page<ProjectListResponseDTO> empty = projectPage.map(p -> ProjectListResponseDTO.builder().build());
            return PageResponse.from(empty);
        }

        //포지션, 기술스택 벌크 조회(n+1방지)
        List<ProjectPosition> posEntities =
                projectPositionRepository.findAllByProfilePksFetchPosition(profilePks);
        List<ProjectTechStack> techEntities =
                projectTechStackRepository.findAllByProfilePksFetchTech(profilePks);

        /** 포지션 그룹핑 */
        //포지션 DTO 리스트로 매핑할 Map
        Map<Long, List<PositionResponseDTO>> posMap = new HashMap<>();
        
        //DB에서 벌크로 가져온 proiectPosition(posEntities)를 하나씩 처리
        for (ProjectPosition pp : posEntities) {
            //projectPosition이 속한 projectProfile의 PK추출
            Long key = pp.getProjectProfile().getProjectProfilePk();
            //해당 ProfilePk키가 없으면 List 새로 생성 / 키가 있으면 기존 리스트 반환
            //-> profilePk에 해당하는 리스트가 있든 없든 항상 append할 수 있는 List<PositionResponseDTO>를 얻을 수 있음
            posMap.computeIfAbsent(key, k -> new ArrayList<>())
                    //반환된 리스트에 새로운 positionResponseDTO 추가
                    //엔티티에서 필요한 값만 꺼내서 dto로 변환
                    .add(new PositionResponseDTO(
                            pp.getPosition().getPositionPk(),
                            pp.getPosition().getPositionName()
                    ));
        }
        /** 기술스택 그룹핑 */
        Map<Long, List<TechStackResponseDTO>> techMap = new HashMap<>();
        for (ProjectTechStack pts : techEntities) {
            Long key = pts.getProjectProfile().getProjectProfilePk();
            techMap.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(new TechStackResponseDTO(
                            pts.getTechStack().getTechPk(),
                            pts.getTechStack().getTechName()
                    ));
        }

        //페이지의 각 project를 dto로 변환
        Page<ProjectListResponseDTO> dtoPage = projectPage.map(project -> {
            ProjectProfile profile = project.getProjectProfile();
            Long profilePk = profile.getProjectProfilePk();

            return ProjectListResponseDTO.builder()
                    .projectPk(project.getProjectPk())
                    .projectProfilePk(profilePk)
                    .userPk(project.getUser().getUserPk())
                    .nickname(project.getUser().getNickname())
                    .projectProfileTitle(profile.getProjectProfileTitle())
                    .projectStatus(project.getProjectStatus().name())
                    .positions(posMap.getOrDefault(profilePk, List.of()))       // 그룹핑한 값 주입
                    .techStacks(techMap.getOrDefault(profilePk, List.of()))     // 그룹핑한 값 주입
                    .recruitmentStartDate(profile.getRecruitmentStartDate())
                    .recruitmentEndDate(profile.getRecruitmentEndDate())
                    .expectedMembers(profile.getExpectedMembers())
                    .build();
        });

        return PageResponse.from(dtoPage);
    }

    /**
     * 프로젝트 생성폼
     * */
    @Transactional(readOnly = true)
    @Override
    public ProjectCreateFormDTO getProjectCreateFormInfo(Long userPk){

        //user 조회
        User user =userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

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

    /**
     * 프로젝트 생성(생성 순서 : 프로젝트 -> 프로젝트 프로필 -> 이미지 ->프로젝트 모집인원 -> 프로젝트 기술스택 -> 프로젝트 포지션 )
     * 프로젝트 생성 시, 프로젝트 프로필 타이틀이 프로젝트 타이틀로 저장됨.
     * */
    @Override
    @Transactional
    public Long createdProject(ProjectCreateRequestDTO dto, List<MultipartFile> images, Long userPk) {

        //user 조회
        User user = userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

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

        //커밋 후 프로젝트 목록 캐시만 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.LIST_ONLY));

        return project.getProjectPk();
    }


    /*
     * 프로젝트 삭제
     * */
    @Override
    @Transactional
    public void deleteProject(Long projectPk, Long userPk){

        //user 조회
        userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

      //프로젝트 조회
      Project project = projectRepository.findById(projectPk)
              .orElseThrow(()-> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

      //삭제 여부 확인
      if("Y".equals(project.getDeletedYN())){
          throw new CustomException(CustomException.ErrorCode.PROJECT_ALREDAY_DELETE);
      }

      //작성자와 로그인한 사용자 일치 여부 -> 같지않으면 예외 발생
        if(!project.getUser().getUserPk().equals(userPk)){
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }

        //project, projectProfile, comment soft delete
        project.markDeleted();
        ProjectProfile projectProfile = project.getProjectProfile();
        projectProfile.markDeleted();
        for(Comment comment : projectProfile.getComments()){
            comment.markDeleted();
        }

        /** 프로젝트 삭제 시 연관된 채팅방 모두 Soft Delete **/
        chattingService.softDeleteAllChattingRoomsByProject(projectPk, userPk);
        /***********************************************/
        
        //hard Delete : 워크스페이스 관련 엔티티는 추후 삭제 추가예정(board_in_project,comment_in_project,github_timeline,s3_file,chatting_room,chatting_message,chatting_participant)
        applyTechStackRepository.deleteByApply_Project_ProjectPk(projectPk);                                        //신청서 기술 스택
        applyRepository.deleteByProject_ProjectPk(projectPk);                                                       //신청서
        projectMemberRepository.deleteByProject_ProjectPk(projectPk);                                               //프로젝트 인원
        likeRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());               //좋아요
        projectTechStackRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());   //프로젝트 기술스택
        projectPositionRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());    //프로젝트 모집분야
        s3Repository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());                //s3이미지

        //프로젝트 삭제시 워크스페이스 게시글, 댓글, 이미지, 파일 삭제
        List<BoardInProject> boardList = boardInProjectRepository.findByProject_ProjectPk(projectPk);
        for (BoardInProject board : boardList) {
            // 이미지 하드 삭제
            List<S3Image> images = boardImageRepository.findByBoardInProject(board);
            boardImageRepository.deleteAll(images);

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));
            // 파일 하드 삭제
            List<S3File> files = boardFileRepository.findByBoardInProject(board);
            boardFileRepository.deleteAll(files);

            // 댓글 소프트 삭제
            List<CommentInProject> commentList = commentInProjectRepository.findByBoardInProject(board);
            for (CommentInProject comment : commentList) {
                comment.markDeleted();
            }
            commentInProjectRepository.saveAll(commentList);

            // 게시글 소프트 삭제
            board.markDeleted();
        }
        boardInProjectRepository.saveAll(boardList);
        //프로젝트 내 일정관리 하드삭제
        scheduleRepository.deleteByProject_ProjectPk(projectPk);

    }


    /**
     * 프로젝트 수정폼 불러오는 메서드
     * */
    @Override
    @Transactional(readOnly = true)
    public ProjectUpdateFormDTO getProjectUpdateForm(Long projectPk, Long userPk) {
        //user 조회
        userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findByIdWithProfile(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        //작성자와 로그인한 사용자 일치 여부 -> 같지않으면 예외 발생
        if(!project.getUser().getUserPk().equals(userPk)){
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }

        ProjectProfile profile = project.getProjectProfile();
        Long profilePk = profile.getProjectProfilePk();

        //기술스택, 포지션 : pk+이름까지 조회
        List<TechStackResponseDTO> techStacks = projectTechStackRepository.findTechStacksByProfilePk(profilePk);
        List<PositionResponseDTO> positions = projectPositionRepository.findPositionsByProfilePk(profilePk);

        // 연관된 기술 스택 및 포지션 pk 조회
        List<Long> techPks = projectTechStackRepository.findTechPksByProfilePk(profilePk);
        List<Long> positionPks = projectPositionRepository.findPositionPksByProfilePk(profilePk);

        //연관된 이미지 조회
        List<ProjectImageDTO> images = s3Repository.findAllImagesByProfilePk(profilePk);

        return ProjectUpdateFormDTO.builder()
                .projectPk(projectPk)
                .projectProfilePk(project.getProjectProfile().getProjectProfilePk())
                .userPk(project.getUser().getUserPk())
                .projectTitle(project.getProjectTitle())
                .projectProfileTitle(profile.getProjectProfileTitle())
                .projectProfileContent(profile.getProjectProfileContent())
                .projectStartDate(project.getProjectStartDate())
                .projectEndDate(project.getProjectEndDate())
                .recruitmentStartDate(profile.getRecruitmentStartDate())
                .recruitmentEndDate(profile.getRecruitmentEndDate())
                .expectedMembers(profile.getExpectedMembers())
                .progressType(profile.getProgressType())
//                .techPks(techPks)
//                .positionPks(positionPks)
                .techStacks(techStacks)
                .positions(positions)
                .images(images)
                .build();
    }

    /**
     * 프로젝트 수정
     */
    @Override
    @Transactional
    public ProjectResponseDTO updatedProject(Long projectPk, ProjectUpdateFormDTO dto, List<MultipartFile> newImages, Long userPk) {

        //user 조회
        userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        //프로젝트 + 프로필 조회
        Project project = projectRepository.findByIdWithProfile(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));
        ProjectProfile projectProfile = project.getProjectProfile();

        //작성자와 로그인한 사용자 일치 여부 -> 같지않으면 예외 발생
        if(!project.getUser().getUserPk().equals(userPk)){
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }
        
        //프로젝트 상태가 종료일땐 수정 불가
        if(project.getProjectStatus() == ProjectStatus.COMPLETED){
            throw new CustomException(CustomException.ErrorCode.PROJECT_NOT_UPDATE);
        }

        //프로젝트, 프로젝트 프로필 update(dirty checking) -> 프로젝트명은 프로젝트 프로필명과 동일.
        project.updateIfChanged(dto);
        projectProfile.updateIfChanged(dto);

        /*
        * 이미지 수정 로직
        * */
        //현재 DB에 저장된 이미지를 List형태로 existingImages에 저장
        List<S3Image> existingImages = s3Repository.findByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());

        //DTO에서 넘어온 유지할 이미지 PK 목록을 SET형식으로 저장
        Set<Long> remainImageIds = dto.getImages() == null ?
                Collections.emptySet() :
                dto.getImages().stream()
                        .map(ProjectImageDTO::getS3ImagePk)
                        .collect(Collectors.toSet());

        //삭제 대상 이미지 목록
        List<S3Image> imagesToDelete = existingImages.stream()
                .filter(img -> !remainImageIds.contains(img.getS3ImagePk()))
                .toList();

        //Hard Delete : S3 -> DB 삭제
        for (S3Image image : imagesToDelete) {
            //s3에서 삭제
            s3Service.deleteImageByUrl(image.getUrl());

            //db에서 삭제
            s3Repository.delete(image);
        }

        //새 이미지 업로드 및 저장
        if (newImages != null && !newImages.isEmpty()) {
            List<String> uploadedUrls = s3Service.uploadFile(newImages, "images");
            for (String url : uploadedUrls) {
                s3Repository.save(S3Image.builder().url(url).projectProfile(projectProfile).build());
            }
        }

        /*기술스택 변경 감지 + 변경된부분이 있으면 삭제 후 재삽입
        */
        //현재 저장된 기술스택들을 DB에서 조회
        List<Long> currentTechPks = projectTechStackRepository.findTechPksByProfilePk(projectProfile.getProjectProfilePk());
        List<Long> currentPositionPks = projectPositionRepository.findPositionPksByProfilePk(projectProfile.getProjectProfilePk());

        boolean techChanged = !new HashSet<>(dto.getTechPks()).equals(new HashSet<>(currentTechPks));
        boolean positionChanged = !new HashSet<>(dto.getPositionPks()).equals(new HashSet<>(currentPositionPks));

        if (techChanged) {
            projectTechStackRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());
            List<ProjectTechStack> toSave = dto.getTechPks().stream()
                    .map(pk -> ProjectTechStack.builder()
                            .projectProfile(projectProfile)
                            .techStack(stackRepository.getReferenceById(pk))
                            .build())
                    .toList();
            projectTechStackRepository.saveAll(toSave);
            currentTechPks = dto.getTechPks();
        }

        if (positionChanged) {
            projectPositionRepository.deleteByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk());
            List<ProjectPosition> toSave = dto.getPositionPks().stream()
                    .map(pk -> ProjectPosition.builder()
                            .projectProfile(projectProfile)
                            .position(positionRepository.getReferenceById(pk))
                            .build())
                    .toList();
            projectPositionRepository.saveAll(toSave);
            currentPositionPks = dto.getPositionPks();
        }

        // 응답: 불필요한 재조회 제거
        List<ProjectTechStack> techStacks =
                techChanged ? projectTechStackRepository.findByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk())
                        : currentTechPks.stream()
                        .map(pk -> ProjectTechStack.builder()
                                .projectProfile(projectProfile)
                                .techStack(stackRepository.getReferenceById(pk))
                                .build())
                        .toList();

        List<ProjectPosition> positions =
                positionChanged ? projectPositionRepository.findByProjectProfile_ProjectProfilePk(projectProfile.getProjectProfilePk())
                        : currentPositionPks.stream()
                        .map(pk -> ProjectPosition.builder()
                                .projectProfile(projectProfile)
                                .position(positionRepository.getReferenceById(pk))
                                .build())
                        .toList();

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));

        return ProjectResponseDTO.fromEntity(project, techStacks, positions);
    }

    /**
    * 프로젝트 상태 변경(모집 -> 진행중)
    * */

    //기획 -> 모집 상태 변경
    @Override
    @Transactional
    public void toRecruiting(Long userPk, Long projectPk){
        Project project = checkValid(userPk, projectPk);
        project.recruiting();

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // 채팅방 생성
        ChattingRoom teamChattingRoom = chattingService.createTeamChattingRoom(project, RoomType.T, now);

        // 팀장 자동 채팅방 참여
        ProjectMember leader = projectMemberRepository.findLeaderByProjectPk(project)
                .orElseThrow(() -> new IllegalStateException("리더가 없습니다."));
        chattingService.addChattingParticipant(teamChattingRoom, leader.getUser().getUserPk(), now);

//        // 캐시 삭제
//        evictProjectCaches(projectPk);

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));


    }

    //모집 -> 진행중 상태 변경
    @Override
    @Transactional
    public void toInProgress(Long userPk, Long projectPk){
        Project project = checkValid(userPk, projectPk);

        //프로젝트 상태 진행중으로 변경
        project.progress();

        //대기중인 신청서 모두 조회 -> 모든 신청서를 거절
        List<Apply> pendingApplies = applyRepository.findPendingAppliesByProjectPk(projectPk);
        for (Apply apply : pendingApplies) {
            apply.reject();
        }

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));
    }

    //진행중 -> 충원
    @Override
    @Transactional
    public void toAdding(Long userPk, Long projectPk){
        Project project = checkValid(userPk, projectPk);
        project.adding();

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));
    }

    //진행중 -> 종료
    @Override
    @Transactional
    public void toCompleted(Long userPk, Long projectPk){
        Project project = checkValid(userPk, projectPk);
        project.complete();

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));
    }

    /**
     * 프로젝트명 변경
     */
    @Override
    @Transactional
    public void updateProjectTitle(Long userPk, Long projectPk, String newTitleRaw){
        Project project = checkValid(userPk, projectPk);

        String newTitle = newTitleRaw == null ? "" : newTitleRaw.trim();
        if(newTitle.isEmpty()){
            throw new CustomException(CustomException.ErrorCode.INVALID_INPUT_VALUE);
        }
        if(project.getProjectStatus() == ProjectStatus.COMPLETED){
            throw new CustomException(CustomException.ErrorCode.PROJECT_TITLE_CHANGE);
        }

        //동일값이면 변경 방지
        if (newTitle.equals(project.getProjectTitle())) {
            return;
        }

        project.updateProjectTitle(newTitle);

        //커밋 후 프로젝트 목록 + 상세 캐시 무효화
        publisher.publishEvent(new ProjectCacheEvictEvent(project.getProjectPk(), EvictScope.DETAIL_AND_LIST));
    }

    /**
     * (팀원)프로젝트 탈퇴 메서드
     */
    @Override
    @Transactional
    public void leaveProject(Long userPk, Long projectPk){
        userRepository.findByIdWithTechStacks(userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        ProjectMember member = projectMemberRepository
                .findByProject_ProjectPkAndUser_UserPk(projectPk, userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.MEMBER_NOT_FOUND));

        //팀장이면 예외처리
        if(member.getIsLeader() == IsLeader.LEADER){
            throw new CustomException(CustomException.ErrorCode.LEADER_CANNOT_LEAVE);
        }

        projectMemberRepository.delete(member);

        /** 팀을 떠나려는 멤버가 속한 모든 채팅방에서 해당 멤버 탈퇴 처리 (팀/개인)**/
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        //채팅방에서도 해당 멤버 퇴장 처리
        chattingService.performRemoveUserFromAllChattingRooms(project.getProjectPk(), member.getUser().getUserPk(), now);
    }

    /**
     * (팀장)프로젝트 강퇴 메서드
     */
    @Override
    @Transactional
    public void kickMember(Long loginUserPk, Long projectPk, Long targetUserPk){
        Project project = checkValid(loginUserPk, projectPk);

        //자기 자신을 강퇴하려는 경우 방지
        if(loginUserPk.equals(targetUserPk)){
            throw new CustomException(CustomException.ErrorCode.INVALID_INPUT_VALUE);
        }

        //대상 멤버 조회
        ProjectMember target = projectMemberRepository
                .findByProject_ProjectPkAndUser_UserPk(projectPk, targetUserPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.MEMBER_NOT_FOUND));

        //대상이 팀장인 경우 금지
        if (target.getIsLeader() == IsLeader.LEADER) {
            throw new CustomException(CustomException.ErrorCode.LEADER_CANNOT_LEAVE);
        }

        //삭제
        int deleted = projectMemberRepository.deleteByProjectPkAndTargetUserPk(projectPk, targetUserPk);
        if(deleted == 0){
            //동시성 방지
            throw new CustomException(CustomException.ErrorCode.MEMBER_NOT_FOUND);
        }

        /** target 멤버가 속한 모든 채팅방에서 해당 멤버 탈퇴 처리 (팀/개인)**/
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        //채팅방에서도 해당 멤버 퇴장 처리
        chattingService.performRemoveUserFromAllChattingRooms(project.getProjectPk(), target.getUser().getUserPk(), now);
    }

    /*
    * 해당 프로젝트에 참여중인 인원 조회
    * */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectMemberListDTO> getProjectMembers(Long projectPk){
        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        return projectMemberRepository.findMemberListByProjectPk(projectPk);
    }



    //user, project 유효성 체크 및 팀장 여부 확인 메서드
    public Project checkValid(Long userPk, Long projectPk){
        userRepository.findByIdWithTechStacks(userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        //팀장 권한 검증
        boolean isLeader = projectMemberRepository
                .existsByProject_ProjectPkAndUser_UserPkAndIsLeader(projectPk, userPk, IsLeader.LEADER);
        if (!isLeader) {
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }
        return project;
    }




    @Override
    public List<CompletedProjectSummaryDto> getCompletedProjectSummaryList(Long userPk){

        List<CompletedProjectSummaryDto> completedProjectSummaryList = projectRepository.findCompletedProjectsByUserPk(userPk);

        // pk 값 추출
        List<Long> projectPkList = completedProjectSummaryList.stream().map(CompletedProjectSummaryDto::getProjectPk).toList();
        List<Long> projectProfilePkList = completedProjectSummaryList.stream().map(CompletedProjectSummaryDto::getProjectProfilePk).toList();

        // 프로젝트 참여 수
        List<ProjectMemberCountDto> projectMemberCount = projectMemberRepository.findProjectMemberCountByProjectPk(projectPkList);
        Map<Long, Long> projectMemberCountMap = projectMemberCount.stream()
                .collect(Collectors.toMap(ProjectMemberCountDto::getProjectPk, ProjectMemberCountDto::getMemberCount));

        // 프로젝트 기술 스택
        List<ProjectTechStackDto> projectTechStack = projectTechStackRepository.findTechStacksByProfilePkIn(projectProfilePkList);
        Map<Long, List<TechStackResponseDTO>> projectTechStackMap = projectTechStack.stream()
                .collect(Collectors.groupingBy(
                        ProjectTechStackDto::getProjectProfilePk,
                        Collectors.mapping(
                                dto -> new TechStackResponseDTO(dto.getTechPk(), dto.getTechName()),
                                Collectors.toList()
                        )
                ));

        // 멤버 리뷰
        List<MemberReviewMypageDto> memberReviewList = memberReviewRepository.findMemberReviewByProjectPkIn(userPk, projectPkList);
        Map<Long, List<String>> reviewMap = memberReviewList.stream()
                .collect(Collectors.groupingBy(
                        MemberReviewMypageDto::getProjectPk,
                        Collectors.mapping(
                                MemberReviewMypageDto::getReview,
                                Collectors.toList()
                        )
                ));

        for (CompletedProjectSummaryDto dto : completedProjectSummaryList) {
            dto.setMemberCount(projectMemberCountMap.get(dto.getProjectPk()));
            dto.setTechStack(projectTechStackMap.get(dto.getProjectProfilePk()));
            dto.setReview(reviewMap.get(dto.getProjectPk()));
        }
        log.info("get project summary list completed :");
        return completedProjectSummaryList;
    }




    @Override
    @Transactional
    public void handleUserWithdrawal(User user) {

        deleteProjectsByLeader(user);

        deleteProjectsByMember(user);
    }

    private void deleteProjectsByLeader(User user){
        log.info("deleted projects by leader");
        // 삭제할 프로젝트 조회
        List<Project> projectsDeleteList = projectRepository.findAllByUser(user);

        // 삭제할 프로젝트가 없으면 반환
        if (projectsDeleteList.isEmpty()) {
            log.info("project withdraw - no project");
            return;
        }

        List<Long> projectPkList = projectsDeleteList.stream().map(Project::getProjectPk).toList();

        List<Long> projectProfilePkList = projectsDeleteList.stream().map(p -> p.getProjectProfile().getProjectProfilePk()).toList();

        // 하드 딜리트
        // s3
        s3Repository.deleteByProjectProfile_ProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - s3_image delete");
        // like
        likeRepository.deleteByProjectProfile_ProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - like delete");
        // 프로젝트 기술 스택
        projectTechStackRepository.deleteByProjectProfile_ProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - tech stack delete");
        // 지원서
        applyService.deleteApplyByProjectPkInBulk(projectPkList);
        log.info("project delete - apply delete");
        // 프로젝트 포지션
        projectPositionRepository.deleteByProjectProfile_ProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - project position delete");
        // 프로젝트 멤버
        projectMemberRepository.deleteByProject_ProjectPkInBulk(projectPkList);
        log.info("project delete - project member delete");

        // s3 파일
        boardFileRepository.deleteByProjectPkInBulk(projectPkList);
        // 댓글 인 프로젝트
        commentInProjectRepository.deleteByProjectPkInBulk(projectPkList);


        // 소프트 딜리트
        //board_in_project
        boardInProjectRepository.softDeleteByProjectPkInBulk(projectPkList);
        // 댓글
        commentRepository.softDeleteByProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - comment soft_delete");
        // 프로젝트 프로필
        projectProfileRepository.softDeleteByProjectProfilePkInBulk(projectProfilePkList);
        log.info("project delete - project_profile soft_delete");
        // 프로젝트
        projectRepository.softDeleteByProjectPkInBulk(projectPkList);
        log.info("project delete - project soft_delete");
        // 채팅방
        chattingRoomRepository.softDeleteByProjectPkInBulk(projectPkList);
        log.info("project delete - chatting_room soft_delete");

        /*

        일정 삭제 추가 해야함

         */
        // 캐싱 무효화
        // 삭제된 프로젝트들이 존재할 때만 무효화 이벤트 발행 : 목록 캐싱 무효화
        if (!projectsDeleteList.isEmpty()) {
            publisher.publishEvent(new ProjectCacheEvictEvent(
                    projectsDeleteList.get(0).getProjectPk(),
                    EvictScope.LIST_ONLY
            ));

            //각 프로젝트 상세 캐시 개별 무효화
            for (Long pk : projectPkList) {
                publisher.publishEvent(new ProjectCacheEvictEvent(pk, EvictScope.DETAIL_AND_LIST));
            }
        }

    }


    private void deleteProjectsByMember(User user){
        log.info("deleted projects by member");


        // 참여중인 프로젝트 워크스페이스 글과 댓글은 삭제 X, 그대로 나뚬

        // 프로젝트 맴버 삭제
        projectMemberRepository.deleteByUserInBulk(user);
        log.info("project delete - project_member delete by user");



    }

    /** 헬퍼 메서드 **/
    // 캐시 삭제를 위한 메서드
    //메서드 상단에서 캐시를 삭제하면 Transactional 과 순서가 꼬여서
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.PROJECT_DETAIL_STATIC, key = "#projectPk"),
            @CacheEvict(cacheNames = CacheNames.PROJECT_LIST,   allEntries = true)
    })
    public void evictProjectCaches(Long projectPk) {
    }
}