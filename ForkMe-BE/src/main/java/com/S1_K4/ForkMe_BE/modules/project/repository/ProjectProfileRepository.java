package com.S1_K4.ForkMe_BE.modules.project.repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.repository
 * @fileName : ProjectProfileRepository
 * @date : 2025-08-07
 * @description : 프로젝트 프로필 Repository
 */
@Repository
public interface ProjectProfileRepository extends JpaRepository<ProjectProfile, Long> {
}