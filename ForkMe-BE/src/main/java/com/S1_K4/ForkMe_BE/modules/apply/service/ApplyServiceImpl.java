package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.apply.dto.*;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.entity.ApplyTechStack;
import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.chatting.chatting_enum.RoomType;
import com.S1_K4.ForkMe_BE.modules.chatting.entity.ChattingRoom;
import com.S1_K4.ForkMe_BE.modules.chatting.service.ChattingService;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectPositionRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.repository.TechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.service
 * @fileName : ApplyServiceImpl
 * @date : 2025-08-11
 * @description : ApplyServiceImpl
 */
@Service
@RequiredArgsConstructor
public class ApplyServiceImpl implements ApplyService{

    private final ProjectPositionRepository projectPositionRepository;
    private final ProjectTechStackRepository projectTechStackRepository;
    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;
    private final ApplyTechStackRepository applyTechStackRepository;
    private final UserRepository userRepository;
    private final TechStackRepository techStackRepository;
    private final UserTechStackRepository userTechStackRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final ChattingService chattingService;

    /**
    * 신청서 생성폼 호출하는 메서드(모집분야, 기술스택 List로 호출)
    * */
    @Override
    @Transactional(readOnly = true)
    public ApplyCreateFormDTO getApplyCreateForm(Long projectPk) {
        projectRepository.findById(projectPk).orElseThrow(()->new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        List<PositionResponseDTO> positions =
                projectPositionRepository.findAllPositionDTOByProfilePk(projectPk);

        List<TechStackResponseDTO> techStacks =
                projectTechStackRepository.findTechStacksByProfilePk(projectPk);

        return ApplyCreateFormDTO.builder()
                .projectPk(projectPk)
                .positions(positions)
                .techStacks(techStacks)
                .build();

    }
    /**
     * 신청서 작성 메서드
     * */
    @Override
    @Transactional
    public ApplyResponseDTO createApply(Long userPk, Long projectPk, ApplyCreateRequestDTO dto) {
        User user = userRepository.findByIdWithTechStacks(userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        Project project = projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        Long profilePk = project.getProjectProfile().getProjectProfilePk();

        //기존 신청서 조회 (PENDING, APPROVED만 차단)
        List<ApplyStatus> blockingStatuses = List.of(ApplyStatus.PENDING, ApplyStatus.APPROVED);
        List<Apply> existing = applyRepository.findBlockingApplies(userPk, projectPk, blockingStatuses);
        if (!existing.isEmpty()) {
            throw new CustomException(CustomException.ErrorCode.ALREADY_APPLIED);
        }

        //권한체크 : 팀장이면 신청서 작성 불가
        boolean isLeader = projectMemberRepository.existsByProject_ProjectPkAndUser_UserPkAndIsLeader(
                projectPk, userPk, IsLeader.LEADER);

        if (isLeader) {
            throw new CustomException(CustomException.ErrorCode.LEADER_CANNOT_APPLY);
        }

        //프로젝트 상태가 모집중, 충원중이 아니면 신청서 작성 불가
        if (!(project.getProjectStatus() == ProjectStatus.RECRUITING
                || project.getProjectStatus() == ProjectStatus.ADDING)) {
            throw new CustomException(CustomException.ErrorCode.APPLY_NOT_WRITTEN);
        }

        // 모집 포지션 검증
        ProjectPosition selectedPosition = projectPositionRepository
                .findByProjectProfile_ProjectProfilePkAndPosition_PositionPk(profilePk, dto.getPositionPk())
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.INVALID_PROJECT_POSITION));

        // 기술스택 검증
        List<Long> validTechIds = projectTechStackRepository.findTechPksByProfilePk(profilePk);
        Set<Long> validSet = new HashSet<>(validTechIds);
        Set<Long> requestedSet = new HashSet<>(dto.getTechStackPks());
        if (!validSet.containsAll(requestedSet)) {
            throw new CustomException(CustomException.ErrorCode.INVALID_TECH_SELECTION);
        }

        //신청서 저장
        Apply apply = Apply.builder()
                .user(user)
                .project(project)
                .projectPosition(selectedPosition)
                .content(dto.getContent())
                .status(ApplyStatus.PENDING)
                .build();

        applyRepository.save(apply);

        //ApplyTechStack 저장
        List<TechStack> techStacks = techStackRepository.findAllById(dto.getTechStackPks());
        List<ApplyTechStack> applyTechStacks = techStacks.stream()
                .map(ts -> ApplyTechStack.builder().apply(apply).techStack(ts).build())
                .toList();
        applyTechStackRepository.saveAll(applyTechStacks);
        apply.getApplyTechStacks().addAll(applyTechStacks);

        return ApplyResponseDTO.from(apply);
    }

    /**
     * 신청서 단건조회 메서드
     * */
    @Override
    @Transactional(readOnly = true)
    public ApplyResponseDTO getApply(Long userPk, Long projectPk, Long applyPk) {

        // 1) 신청서 본문 조회
        Apply apply = applyRepository.findDetailById(applyPk, projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.APPLY_NOT_FOUND));

        // 권한 체크 - 신청자 본인 또는 해당 프로젝트의 팀장만 허용
        boolean isUser = apply.getUser().getUserPk().equals(userPk);
        boolean isLeader = projectMemberRepository.existsByProject_ProjectPkAndUser_UserPkAndIsLeader(
                projectPk, userPk, IsLeader.LEADER);

        if (!isUser && !isLeader) {
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }

        // 2) 신청서 기술스택 -> DTO 매핑
        List<ApplyResponseDTO.TechStackInfo> applyStacks = apply.getApplyTechStacks().stream()
                .map(ats -> ApplyResponseDTO.TechStackInfo.builder()
                        .techPk(ats.getTechStack().getTechPk())
                        .techName(ats.getTechStack().getTechName())
                        .build())
                .toList();

        // 3) 유저 전체 기술스택을 별도 쿼리로 조회 -> DTO 매핑
        Long applicantUserPk = apply.getUser().getUserPk(); //신청자의 기술스택
        List<TechStackResponseDTO> userStackDtos = userTechStackRepository.findUserTechStackByUserPk(applicantUserPk);

        List<ApplyResponseDTO.TechStackInfo> userStacks = userStackDtos.stream()
                .map(ts -> ApplyResponseDTO.TechStackInfo.builder()
                        .techPk(ts.getTechPk())
                        .techName(ts.getTechName())
                        .build())
                .toList();

        // 4) 최종 DTO 빌드
        return ApplyResponseDTO.builder()
                .applyPk(apply.getApplyPk())
                .projectPk(apply.getProject().getProjectPk())
                .userPk(applicantUserPk)
                .nickname(apply.getUser().getNickname())
                .content(apply.getContent())
                .positionName(apply.getProjectPosition().getPosition().getPositionName())
                .techStacks(applyStacks)
                .userTechStacks(userStacks)  // <- 별도 쿼리 결과 사용
                .status(apply.getStatus().getDescription())
                .createdAt(apply.getCreatedAt())
                .build();
    }

    /**
     * (팀장)해당 프로젝트의 모든 신청서 내역 조회
     * */
    @Override
    @Transactional(readOnly = true)
    public List<ApplyListResponseDTO> getProjectApplies(Long userPk, Long projectPk) {
        checkValid(userPk, projectPk);

        //신청서 목록 조회
        return applyRepository.findAllByProjectPk(projectPk).stream()
                .map(a -> ApplyListResponseDTO.builder()
                        .applyPk(a.getApplyPk())
                        .nickname(a.getUser().getNickname())
                        .profileUrl(a.getUser().getProfileUrl())
                        .userPk(a.getUser().getUserPk())
                        .status(a.getStatus().getDescription())
                        .createdAt(a.getCreatedAt())
                        .projectPk(a.getProject().getProjectPk())
                        .build()
                )
                .toList();
    }

    /**
     * 작성한 신청서 취소하는 메서드
     * */
    @Override
    @Transactional
    public void cancelApply(Long userPk, Long projectPk, Long applyPk){
        userRepository.findByIdWithTechStacks(userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        Apply apply = applyRepository.findByApplyPkAndProject_ProjectPk(applyPk, projectPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.APPLY_NOT_FOUND));

        apply.cancel();
    }

    /**
     * 신청서 수락 메서드(팀장만 가능)
     * */
    @Override
    @Transactional
    public void approveApply(Long userPk, Long projectPk, Long applyPk){
        //이 부분에서 채팅이 project, user 엔티티를 필요로 해서 어쩔 수 없이 checkValid() 메서드를 사용하는 부분을 변경했습니다.
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


        Apply apply = applyRepository.findByApplyPkAndProject_ProjectPk(applyPk, projectPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.APPLY_NOT_FOUND));

        apply.approve();

        //신청서 신청한 유저(팀원)
        User applicant = apply.getUser();
        Long applicantUserPk = applicant.getUserPk();

        //이미 멤버면 스킵
        if (projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(projectPk, applicantUserPk)) {
            return;
        }

        //projectMember 레포지토리 추가
        ProjectMember newMember = ProjectMember.memberOf(project, applicant);
        try {
            projectMemberRepository.save(newMember);
        } catch (DataIntegrityViolationException e) {
            if (!projectMemberRepository.existsByProject_ProjectPkAndUser_UserPk(projectPk, applicantUserPk)) {
                throw e; // 정말 저장 실패라면 전파
            }
        }

        /** 추가된 멤버와 기존 멤버간 개인 채팅방 자동 생성 및 멤버 추가 **/
        //멤버 추가 시간 가져오기
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        //프로젝트 팀 채팅방 가져오기
        ChattingRoom teamChattingRoom = chattingService.getChattingRoom(project.getProjectPk(), RoomType.T);

        //프로젝트 팀 채팅방에 수락 멤버 추가하기
        chattingService.addChattingParticipant(teamChattingRoom, applicantUserPk, now);

        //프로젝트 팀 채팅방에 수락 멤버 입장 메세지 전송 및 DB 저장
        chattingService.noticeJoinChattingRoom(teamChattingRoom, applicant, now);


        /**기존 멤버들과 개인 채팅방 자동 생성 로직 **/
        chattingService.createAllPrivateRoomsForNewMember(project, applicant, now);


    }

    /**
    * 신청서 거절 메서드(팀장만 가능)
    * */
    @Override
    @Transactional
    public void rejectedApply(Long userPk, Long projectPk, Long applyPk){
        checkValid(userPk, projectPk);

        Apply apply = applyRepository.findByApplyPkAndProject_ProjectPk(applyPk, projectPk)
                .orElseThrow(()-> new CustomException(CustomException.ErrorCode.APPLY_NOT_FOUND));

        apply.reject();
    }

    /**
     * user, project, 팀장 검증 메서드
     * */
    public void checkValid(Long userPk, Long projectPk){
        userRepository.findByIdWithTechStacks(userPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        projectRepository.findById(projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        //팀장 권한 검증
        boolean isLeader = projectMemberRepository
                .existsByProject_ProjectPkAndUser_UserPkAndIsLeader(projectPk, userPk, IsLeader.LEADER);
        if (!isLeader) {
            throw new CustomException(CustomException.ErrorCode.FORBIDDEN);
        }
    }



    @Override
    public List<MyApplyListResponseDto> getMyApplyList(Long userPk, List<String> statusList) {

        List<MyApplyListResponseDto> applyList = applyRepository.findApplyByUserPkInState(userPk, statusList);

        List<Long> applyPkList = applyList.stream().map(MyApplyListResponseDto::getApplyPk).toList();

        List<ApplyTechStackDto> applyTechStackList = applyTechStackRepository.findApplyTechStacksByApplyPkIn(applyPkList);
        Map<Long, List<TechStackResponseDTO>> applyTechStackMap = applyTechStackList.stream()
                .collect(Collectors.groupingBy(
                        ApplyTechStackDto::getApplyPk,
                        Collectors.mapping(
                                dto -> new TechStackResponseDTO(dto.getTechPk(), dto.getTechName()),
                                Collectors.toList()
                        )
                ));

        for (MyApplyListResponseDto myApplyListResponseDto : applyList) {
            myApplyListResponseDto.setTechStacks(applyTechStackMap.get(myApplyListResponseDto.getApplyPk()));
        }

        return applyList;
    }

}
