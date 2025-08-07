package com.S1_K4.ForkMe_BE.modules.user.repository;

import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.entity.UserTechStack;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.repository
 * @fileName : UserTechStackRepository
 * @date : 2025-08-06
 * @description : 유저기술스텍 레포지토리
 */

@Repository
public interface UserTechStackRepository extends JpaRepository<UserTechStack, Long> {
    void deleteAllByUser(User user);

    @Query(
            "SELECT new com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackDto(ts.techPk, ts.techName) " +
                    "FROM UserTechStack uts JOIN TechStack ts ON uts.techStack.techPk = ts.techPk " +
                    "WHERE uts.user.userPk = :userPk"
    )
    List<TechStackDto> findUserTechStackByUserPk(@Param("userPk") Long userPk);

}
