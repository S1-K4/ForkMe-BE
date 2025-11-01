package com.S1_K4.ForkMe_BE.global.elasticsearch.service;

import com.S1_K4.ForkMe_BE.global.elasticsearch.dto.ProjectEsDocument;
import com.S1_K4.ForkMe_BE.global.elasticsearch.dto.ProjectEsListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.elasticsearch.service
 * @fileName : ProjectEsService
 * @date : 2025-10-31
 * @description : Elasticsearch 프로젝트 검색 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectEsService {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 통합 검색 메서드 - 모든 파라미터는 선택적(optional)
     *
     * @param keyword 검색 키워드 (프로젝트명, 닉네임 등)
     * @param techStacks 기술 스택 리스트
     * @param positions 모집 분야 리스트
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 페이징 처리된 검색 결과
     */
    public Page<ProjectEsListDTO> searchProjects(
            String keyword,
            List<String> techStacks,
            List<String> positions,
            int page,
            int size
    ) {
        Criteria criteria = new Criteria();

        // 1. 키워드 검색 (프로젝트명, 닉네임에서 검색)
        if (keyword != null && !keyword.trim().isEmpty()) {
            Criteria keywordCriteria = new Criteria()
                    .or("projectProfileTitle").contains(keyword)
                    .or("nickname").contains(keyword);
            criteria = criteria.and(keywordCriteria);
        }

        // 2. 기술 스택 필터
        if (techStacks != null && !techStacks.isEmpty()) {
            Criteria techStackCriteria = new Criteria("techStacks.techName")
                    .in(techStacks);
            criteria = criteria.and(techStackCriteria);
        }

        // 3. 모집 분야 필터
        if (positions != null && !positions.isEmpty()) {
            Criteria positionCriteria = new Criteria("positions.positionName")
                    .in(positions);
            criteria = criteria.and(positionCriteria);
        }

        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);

        // 쿼리 생성 및 실행
        Query query = new CriteriaQuery(criteria).setPageable(pageable);

        SearchHits<ProjectEsDocument> searchHits = elasticsearchOperations.search(
                query,
                ProjectEsDocument.class
        );

        // 결과 변환
        List<ProjectEsListDTO> content = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ProjectEsListDTO::toProjectEsDTO)
                .collect(Collectors.toList());

        // Page 객체로 변환하여 반환
        return new PageImpl<>(content, pageable, searchHits.getTotalHits());
    }
}