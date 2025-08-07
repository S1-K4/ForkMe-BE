package com.S1_K4.ForkMe_BE.modules.s3.Repository;

import com.S1_K4.ForkMe_BE.modules.s3.Entity.S3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.Repository
 * @fileName : S3Repository
 * @date : 2025-08-07
 * @description : S3 레포지토리
 */
@Repository
public interface S3Repository extends JpaRepository<S3Image, Long> {

}
