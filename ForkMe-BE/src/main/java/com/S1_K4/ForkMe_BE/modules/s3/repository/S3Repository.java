package com.S1_K4.ForkMe_BE.modules.s3.repository;

import com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO;
import com.S1_K4.ForkMe_BE.modules.s3.entity.S3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.repository
 * @fileName : S3Repository
 * @date : 2025-08-07
 * @description : S3 레포지토리
 */
@Repository
public interface S3Repository extends JpaRepository<S3Image, Long> {

    //해당 프로필의 이미지 필드 삭제
    void deleteByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //해당 프로필의 이미지필드 조회
    List<S3Image> findByProjectProfile_ProjectProfilePk(Long projectProfilePk);

    //해당 프로필의 이미지 필드를 dto로 반환
    @Query("""
           select new com.S1_K4.ForkMe_BE.modules.s3.dto.ProjectImageDTO(i.s3ImagePk, i.url)
           from S3Image i
           where i.projectProfile.projectProfilePk = :profilePk
           AND i.boardInProject is Null
           """)
    List<ProjectImageDTO> findAllImagesByProfilePk(@Param("profilePk") Long profilePk);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM S3Image i WHERE i.projectProfile.projectProfilePk IN (:projectProfilePkList)")
    void deleteByProjectProfile_ProjectProfilePkInBulk(List<Long> projectProfilePkList);

}
