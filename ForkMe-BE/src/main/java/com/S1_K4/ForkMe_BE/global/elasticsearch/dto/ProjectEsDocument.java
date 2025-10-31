package com.S1_K4.ForkMe_BE.global.elasticsearch.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.elasticsearch.dto
 * @fileName : ProjectEsDocument
 * @date : 2025-10-31
 * @description : Elasticsearch에 저장되는 프로젝트 정보 모델
 */
@JsonIgnoreProperties(ignoreUnknown = true) // 해당 설정을 넣지 않으면 class 속성이 들어가게 됨
@Document(indexName = "project-index")
@Getter
@NoArgsConstructor
public class ProjectEsDocument {
    @Schema(description = "프로젝트 PK")
    private Long projectPk;

    @Schema(description = "프로젝트 프로필 PK")
    private Long projectProfilePk;

    @Schema(description = "프로젝트 팀장 userPK")
    private Long userPk;

    @Schema(description = "프로젝트 팀장 닉네임")
    private String nickname;

    @Schema(description = "프로젝트 프로필 명")
    private String projectProfileTitle;

    @Schema(description = "프로젝트 진행 상황")
    private String projectStatus;

    @Field(type = FieldType.Nested)
    @Schema(description = "프로젝트 모집 분야")
    private List<PositionResponseDTO> positions;

    @Field(type = FieldType.Nested)
    @Schema(description = "프로젝트 기술 스택")
    private List<TechStackResponseDTO> techStacks;

    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;

    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate;

    @Schema(description = "예상 모집 인원")
    private int expectedMembers;
}