package com.S1_K4.ForkMe_BE.modules.s3.Repository;

import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.s3.Entity.S3Image;
import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.Repository
 * @fileName : S3Repository
 * @date : 2025-08-07
 * @description : S3 레포지토리
 */
@Repository
public interface S3Repository extends JpaRepository<S3Image, Long> {

    //해당 프로필의 이미지 필드 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //해당 프로필의 이미지필드 조회
    List<S3Image> findByProjectProfile(ProjectProfile projectProfile);

    @Query("""
           select new com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO(i.s3ImagePk, i.url)
           from S3Image i
           where i.projectProfile.projectProfilePk = :profilePk
           """)
    List<ProjectImageDTO> findAllImagesByProfilePk(@Param("profilePk") Long profilePk);
}
