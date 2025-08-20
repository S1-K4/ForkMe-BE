package com.S1_K4.ForkMe_BE.modules.project.service;

import com.S1_K4.ForkMe_BE.modules.project.dto.*;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.service
 * @fileName : ProjectService
 * @date : 2025-08-05
 * @description : ProjectService
 */
public interface ProjectService {

    //프로젝트 상세 보기
    ProjectDetailResponseDTO getProjectDetail(Long projectPk);

     //프로젝트 목록 조회
    PageResponse<ProjectListResponseDTO> getProjectList(Pageable pageable);


    //프로젝트 생성폼
    ProjectCreateFormDTO getProjectCreateFormInfo(Long userPk);


    //프로젝트 생성(생성 순서 : 프로젝트 -> 프로젝트 프로필 -> 이미지 ->프로젝트 모집인원 -> 프로젝트 기술스택 -> 프로젝트 포지션 )
    Long createdProject(ProjectCreateRequestDTO dto, List<MultipartFile> images, Long userPk);


    //프로젝트 삭제
    void deleteProject(Long projectPk, Long userPk);



    //프로젝트 수정폼
    ProjectUpdateFormDTO getProjectUpdateForm(Long projectPk, Long userPk);


    //프로젝트 수정
    ProjectResponseDTO updatedProject(Long projectPk, ProjectUpdateFormDTO dto, List<MultipartFile> newImages, Long userPk);

    //기획 -> 모집 상태 변경
    void toRecruiting(Long userPk, Long projectPk);

    //모집 -> 진행중 상태 변경
    void toInProgress(Long userPk, Long projectPk);

    //진행중 -> 충원
    void toAdding(Long userPk, Long projectPk);

    //진행중 -> 종료
    void toCompleted(Long userPk, Long projectPk);

    //프로젝트 명 변경
    void updateProjectTitle(Long userPk, Long projectPk, String newTitleRaw);

    //프로젝트 나가기(팀원)
    void leaveProject(Long userPk, Long projectPk);

    //프로젝트 강퇴(팀장)
    void kickMember(Long loginUserPk, Long projectPk, Long targetUserPk);

    /*
    * 해당 프로젝트에 참여중인 인원 조회
    * */
    @Transactional(readOnly = true)
    List<ProjectMemberListDTO> getProjectMembers(Long projectPk);

    /*
     * 완료된 프로젝트 정보
     */
    List<CompletedProjectSummaryDto> getCompletedProjectSummaryList(Long userPk);


    // 회원 탈퇴하면 프로젝트 삭제
    void withdrawUser(User user);

}