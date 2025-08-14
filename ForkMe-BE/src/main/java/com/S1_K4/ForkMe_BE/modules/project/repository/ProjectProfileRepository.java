package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectProfileRepository
 * @date : 2025-08-07
 * @description : 프로젝트 프로필 repository
 */
@Repository
public interface ProjectProfileRepository extends JpaRepository<ProjectProfile, Long> {

    @Modifying( clearAutomatically = true)
    @Query("UPDATE ProjectProfile pp SET pp.deletedYN = 'Y' WHERE pp.projectProfilePk IN (:projectProfilePkList)")
    void softDeleteByProjectProfilePkInBulk(List<Long> projectProfilePkList);
}