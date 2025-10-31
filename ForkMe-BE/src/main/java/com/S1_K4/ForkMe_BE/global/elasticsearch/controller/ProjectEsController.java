package com.S1_K4.ForkMe_BE.global.elasticsearch.controller;

import com.S1_K4.ForkMe_BE.global.elasticsearch.dto.ProjectEsListDTO;
import com.S1_K4.ForkMe_BE.global.elasticsearch.service.ProjectEsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.elasticsearch.controller
 * @fileName : ProjectEsController
 * @date : 2025-10-31
 * @description : Elasticsearch 프로젝트 검색 컨트롤러
 */
@Tag(name = "Project Search", description = "프로젝트 검색 API")
@Slf4j
@RestController
@RequestMapping("/api/projects/search")
@RequiredArgsConstructor
public class ProjectEsController {

    private final ProjectEsService projectEsService;

    @Operation(
            summary = "프로젝트 통합 검색",
            description = "키워드, 기술스택, 모집분야를 선택적으로 조합하여 프로젝트 검색. 모든 파라미터는 선택사항입니다."
    )
    @GetMapping
    public ResponseEntity<Page<ProjectEsListDTO>> searchProjects(
            @Parameter(description = "검색 키워드 (프로젝트명, 닉네임)")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "기술 스택 리스트 (예: Java,Spring,React)")
            @RequestParam(required = false) List<String> techStacks,

            @Parameter(description = "모집 분야 리스트 (예: 백엔드,프론트엔드)")
            @RequestParam(required = false) List<String> positions,

            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "12") int size
    ) {
        log.info("통합 검색 요청 - keyword: {}, techStacks: {}, positions: {}, page: {}, size: {}",
                keyword, techStacks, positions, page, size);

        Page<ProjectEsListDTO> results = projectEsService.searchProjects(
                keyword,
                techStacks,
                positions,
                page,
                size
        );

        return ResponseEntity.ok(results);
    }
}