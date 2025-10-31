package com.S1_K4.ForkMe_BE.global.searhlog.service;

import com.S1_K4.ForkMe_BE.global.searhlog.domain.SearchLogDocument;
import com.S1_K4.ForkMe_BE.global.searhlog.repository.SearchLogEsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * 엘라스틱서치 저장. 통계 집계 비즈니스 로직 서비스
 * @fileName        : SearchLogEsRepository
 * @author          : 선순주
 * @since           : 2025-10-31
 *
 */
@RequiredArgsConstructor
@Service
public class SearchLogEsService {
    private final SearchLogEsRepository searchLogEsRepository;

    //카프카에서 전달 받은 검색데이터 저장 메서드
    public void save(SearchLogDocument searchLogDocument) {
        searchLogEsRepository.save(searchLogDocument);
    }

}
