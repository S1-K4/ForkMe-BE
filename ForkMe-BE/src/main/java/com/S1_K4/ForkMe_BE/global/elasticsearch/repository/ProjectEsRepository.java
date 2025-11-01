package com.S1_K4.ForkMe_BE.global.elasticsearch.repository;

import com.S1_K4.ForkMe_BE.global.elasticsearch.dto.ProjectEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.elasticsearch.repository
 * @fileName : ProjectEsRepository
 * @date : 2025-10-31
 * @description : 엘라스틱서치 관련 repository
 */
@Repository
public interface ProjectEsRepository extends ElasticsearchRepository<ProjectEsDocument, String> {
    //프로젝트 ID로 데이터 삭제
    void deleteById(String id);
}
