package com.S1_K4.ForkMe_BE.global.searhlog.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.searhlog.domain
 * @fileName : SearchLogDocument
 * @date : 2025-10-31
 * @description : 엘라스틱 서치에 저장되는 검색 데이터
 */
@Document(indexName = "search-log-index")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLogDocument {
    @Id
    private String id;
    private String keyword;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private String searchedAt;
}
