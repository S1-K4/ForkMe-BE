package com.S1_K4.ForkMe_BE.modules.apply.repository;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyTechStackDto;
import com.S1_K4.ForkMe_BE.modules.apply.entity.ApplyTechStack;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply
 * @fileName : ApplyTechStackRepository
 * @date : 2025-08-08
 * @description : 신청서 기술스택 repository
 */
@Repository
public interface ApplyTechStackRepository extends JpaRepository<ApplyTechStack, Long> {
    //해당 projectPk를 가지는 신청서 기술스택 삭제
    void deleteByApply_Project_ProjectPk(Long projectPk);

    @Query("SELECT new com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyTechStackDto(ats.apply.applyPk, ats.techStack.techPk, ats.techStack.techName) " +
            "FROM ApplyTechStack ats " +
            "WHERE ats.apply.applyPk IN (:applyPkList)")
    List<ApplyTechStackDto> findApplyTechStacksByApplyPkIn(List<Long> applyPkList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ApplyTechStack ats WHERE ats.apply.project.projectPk IN (:applyPkList)")
    void deleteApplyTechStacksByApply_Project_ProjectPkInBulk(List<Long> projectPkList);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ApplyTechStack ats WHERE ats.apply.user = (:user)")
    void deleteApplyTechStackByApply_UserInBulk(User user);
}
