package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateFormDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyCreateRequestDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyListResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyResponseDTO;
import com.S1_K4.ForkMe_BE.modules.apply.entity.Apply;
import com.S1_K4.ForkMe_BE.modules.apply.entity.ApplyTechStack;
import com.S1_K4.ForkMe_BE.modules.apply.enums.ApplyStatus;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectPosition;
import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectPositionRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectTechStackRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.repository.TechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackDto;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                projectTechStackRepository.findAllTechStackDTOByProfilePk(projectPk);

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

        // 3. 기존 신청서 조회 (PENDING, APPROVED만 차단)
        List<ApplyStatus> blockingStatuses = List.of(ApplyStatus.PENDING, ApplyStatus.APPROVED);
        List<Apply> existing = applyRepository.findBlockingApplies(userPk, projectPk, blockingStatuses);

        if (!existing.isEmpty()) {
            throw new CustomException(CustomException.ErrorCode.ALREADY_APPLIED);
        }


        //모집 포지션 검증
        ProjectPosition selectedPosition = projectPositionRepository
                .findByProjectProfileAndPositionPk(projectPk, dto.getProjectPositionPk())
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.INVALID_PROJECT_POSITION));

        //기술스택 검증
        List<Long> validTechStackPks = projectTechStackRepository.findTechStackIdsByProjectProfilePk(projectPk);

        Set<Long> validSet = new HashSet<>(validTechStackPks);
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
                .map(stack -> ApplyTechStack.builder()
                        .apply(apply)
                        .techStack(stack)
                        .build())
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
    public ApplyResponseDTO getApply(Long projectPk, Long applyPk) {
        // 1) 신청서 본문 조회
        Apply apply = applyRepository.findByApplyPkAndProject_ProjectPk(applyPk, projectPk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.APPLY_NOT_FOUND));

        // 2) 신청서 기술스택 -> DTO 매핑
        List<ApplyResponseDTO.TechStackInfo> applyStacks = apply.getApplyTechStacks().stream()
                .map(ats -> ApplyResponseDTO.TechStackInfo.builder()
                        .techPk(ats.getTechStack().getTechPk())
                        .techName(ats.getTechStack().getTechName())
                        .build())
                .toList();

        // 3) 유저 전체 기술스택을 별도 쿼리로 조회 -> DTO 매핑
        Long userPk = apply.getUser().getUserPk();
        List<TechStackDto> userStackDtos = userTechStackRepository.findUserTechStackByUserPk(userPk);

        List<ApplyResponseDTO.TechStackInfo> userStacks = userStackDtos.stream()
                .map(ts -> ApplyResponseDTO.TechStackInfo.builder()
                        .techPk(ts.getTechStackPk())
                        .techName(ts.getTechStackName())
                        .build())
                .toList();

        // 4) 최종 DTO 빌드
        return ApplyResponseDTO.builder()
                .applyPk(apply.getApplyPk())
                .projectPk(apply.getProject().getProjectPk())
                .userPk(userPk)
                .nickname(apply.getUser().getNickname())
                .content(apply.getContent())
                .positionName(apply.getProjectPosition().getPosition().getPositionName())
                .techStacks(applyStacks)
                .userTechStacks(userStacks)  // <- 별도 쿼리 결과 사용
                .status(apply.getStatus().name())
                .createdAt(apply.getCreatedAt())
                .build();
    }

    /**
     * 내 신청내역 목록 조회
     * */
    @Override
    @Transactional(readOnly = true)
    public List<ApplyListResponseDTO> getProjectApplies(Long userPk, Long projectPk) {
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
        
        //신청서 목록 조회
        return applyRepository.findAllByProjectPk(projectPk).stream()
                .map(a -> ApplyListResponseDTO.builder()
                        .nickname(a.getUser().getNickname())
                        .profileUrl(a.getUser().getProfileUrl())
                        .userPk(a.getUser().getUserPk())
                        .status(a.getStatus())
                        .createdAt(a.getCreatedAt())
                        .projectPk(a.getProject().getProjectPk())
                        .build()
                )
                .toList();
    }
    

}
