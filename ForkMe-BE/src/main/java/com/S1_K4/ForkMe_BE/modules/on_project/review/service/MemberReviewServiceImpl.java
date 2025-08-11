package com.S1_K4.ForkMe_BE.modules.on_project.review.service;

import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewRequest;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewResponse;
import com.S1_K4.ForkMe_BE.modules.on_project.review.entity.MemberReview;
import com.S1_K4.ForkMe_BE.modules.on_project.review.repository.MemberReviewRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    // 멤버 리뷰 작성
   public MemberReviewResponse createReview(Long projectPk, Long writerUserPk, MemberReviewRequest dto){
       Project project = projectRepository.findById(projectPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));

       User writer = userRepository.findById(writerUserPk)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작성자입니다."));

       User target = userRepository.findById(dto.getTargetUserPk())
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기 대상자입니다."));

       MemberReview review = MemberReview.builder()
               .project(project)
               .writer(writer)
               .target(target)
               .review(dto.getReview())
               .build();

       memberReviewRepository.save(review);

       return MemberReviewResponse.from(review);
   }


   public List<MemberReviewResponse> getMyWrittenReviews(Long userPk, Long projectPk){
       List<MemberReview> reviews = memberReviewRepository.findAllByProject_ProjectPkAndWriter_UserPk(projectPk, userPk);
       return MemberReviewResponse.fromList(reviews);
   }

   public List<MemberReviewResponse> getMyReceivedReviews(Long userPk, Long projectPk){
       List<MemberReview> recevies = memberReviewRepository.findAllByProject_ProjectPkAndTarget_UserPk(projectPk, userPk);
       return MemberReviewResponse.fromList(recevies);
   }
}