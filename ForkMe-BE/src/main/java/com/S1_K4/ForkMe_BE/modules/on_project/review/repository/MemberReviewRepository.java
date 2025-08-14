package com.S1_K4.ForkMe_BE.modules.on_project.review.repository;
import com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewMypageDto;
import com.S1_K4.ForkMe_BE.modules.on_project.review.entity.MemberReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.review.repository
 * @fileName : MemberReviewRepository
 * @date : 2025-08-11
 * @description : 멤버 평가 repository
 */

@Repository
public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {
    List<MemberReview> findAllByProject_ProjectPkAndWriter_UserPk(Long projectPk, Long userPk);
    List<MemberReview> findAllByProject_ProjectPkAndTarget_UserPk(Long projectPk, Long userPk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.on_project.review.dto.MemberReviewMypageDto(mr.project.projectPk, mr.review) FROM MemberReview mr WHERE mr.target.userPk = (:userPk) AND mr.project.projectPk IN (:projectPkList)")
    List<MemberReviewMypageDto> findMemberReviewByProjectPkIn(Long userPk, List<Long> projectPkList);
}
