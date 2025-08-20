package com.S1_K4.ForkMe_BE.modules.on_project.review.service;

import com.S1_K4.ForkMe_BE.global.common.cache.CacheNames;
import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.review.entity.MemberReview;
import com.S1_K4.ForkMe_BE.modules.on_project.review.repository.MemberReviewRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectMember;
import com.S1_K4.ForkMe_BE.modules.project.enums.ProjectStatus;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectMemberRepository;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.service
 * @fileName : MemberReviewServiceImpl
 * @date : 2025-08-11
 * @description : 멤버 평가 service
 */

@Builder
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberReviewServiceImpl implements MemberReviewService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MemberReviewRepository memberReviewRepository;
    private final ProjectMemberRepository projectMemberRepository;


    // 멤버 리뷰 작성
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.REVIEW_LIST, key = "'written:' + #writerUserPk + ':' + #projectPk"),
            @CacheEvict(cacheNames = CacheNames.REVIEW_LIST, key = "'writtenAll:' + #writerUserPk"),
            @CacheEvict(cacheNames = CacheNames.REVIEW_LIST, key = "'received:' + #dto.targetUserPk + ':' + #projectPk"),
            @CacheEvict(cacheNames = CacheNames.REVIEW_LIST, key = "'receivedAll:' + #dto.targetUserPk")
    })
    @Transactional
   public MemberReviewResponse createReview(Long projectPk, Long writerUserPk, MemberReviewRequest dto){
       Project project = projectRepository.findById(projectPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
       if (project.getProjectStatus() != ProjectStatus.COMPLETED) {
           throw new IllegalArgumentException("완료되지 않은 프로젝트에는 후기를 작성할 수 없습니다.");
       }
       User writer = userRepository.findById(writerUserPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작성자입니다."));

       User target = userRepository.findById(dto.getTargetUserPk())
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기 대상자입니다."));

       // 해당 유저가 프로젝트 멤버인지 확인
       ProjectMember member = projectMemberRepository
               .findByProject_ProjectPkAndUser_UserPk(projectPk, dto.getTargetUserPk())
               .orElseThrow(() -> new CustomException(CustomException.ErrorCode.MEMBER_NOT_FOUND));

       // 해당 멤버한테 이미 후기를 작성했는지 확인
       boolean alreadyReviewed = memberReviewRepository.existsByProjectAndWriterAndTarget(project, writer, target);
       if (alreadyReviewed) {
           System.out.println("이미 해당 멤버에 대한 후기를 작성하였습니다.");
           throw new IllegalArgumentException("이미 해당 멤버에 대한 후기를 작성하였습니다.");
       }

       MemberReview review = MemberReview.builder()
               .project(project)
               .writer(writer)
               .target(target)
               .review(dto.getReview())
               .build();

       memberReviewRepository.save(review);

       return MemberReviewResponse.from(review);
   }



   // 내가 특정 프로젝트에서 작성한 리뷰
   @Cacheable(
           cacheNames = CacheNames.REVIEW_LIST,
           key = "'written:' + #userPk + ':' + #projectPk"
   )
   public List<MemberReviewResponse> getMyWrittenReviews(Long userPk, Long projectPk){

       Project project = projectRepository.findById(projectPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
       List<MemberReview> reviews = memberReviewRepository.findAllByProject_ProjectPkAndWriter_UserPk(projectPk, userPk);
       return MemberReviewResponse.fromList(reviews);
   }

    // 내가 작성한 모든 리뷰
    @Cacheable(
            cacheNames = CacheNames.REVIEW_LIST,
            key = "'written:' + #userPk"
    )
   public List<MemberReviewResponse> getMyWrittenAllReviews(Long userPk){
       List<MemberReview> reviews = memberReviewRepository.findByWriterUserPk(userPk);

       return MemberReviewResponse.fromList(reviews);

   }

   // 내가 특정 프로젝트에서 받은 리뷰
   @Cacheable(
           cacheNames = CacheNames.REVIEW_LIST,
           key = "'received:' + #userPk + ':' + #projectPk"
   )
   public List<MemberReviewResponse> getMyReceivedReviews(Long userPk, Long projectPk){
       Project project = projectRepository.findById(projectPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

       List<MemberReview> recevies = memberReviewRepository.findAllByProject_ProjectPkAndTarget_UserPk(projectPk, userPk);
       return MemberReviewResponse.fromList(recevies);
   }

   // 내가 받은 모든 리뷰
   @Cacheable(
           cacheNames = CacheNames.REVIEW_LIST,
           key = "'received:' + #userPk"
   )
    public List<MemberReviewResponse> getMyReceivedAllReviews(Long userPk){
        List<MemberReview> reviews = memberReviewRepository.findByTargetUserPk(userPk);

        return MemberReviewResponse.fromList(reviews);

    }
}