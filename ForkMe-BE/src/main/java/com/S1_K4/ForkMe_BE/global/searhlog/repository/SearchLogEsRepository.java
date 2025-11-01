package com.S1_K4.ForkMe_BE.global.searhlog.repository;

import com.S1_K4.ForkMe_BE.global.searhlog.domain.SearchLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.searhlog.repository
 * @fileName : SearchLogEsRepository
 * @date : 2025-10-31
 * @description : 엘라스틱 서치 저장/검색용 레포지토리
 */
public interface SearchLogEsRepository extends ElasticsearchRepository<SearchLogDocument,String> {

}
