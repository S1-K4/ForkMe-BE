package com.S1_K4.ForkMe_BE.global.elasticsearch.dto;

import com.S1_K4.ForkMe_BE.reference.position.dto.PositionResponseDTO;
import com.S1_K4.ForkMe_BE.reference.position.entity.Position;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.elasticsearch.dto
 * @fileName : ProjectEsListDTO
 * @date : 2025-10-31
 * @description : 전체 프로젝트 조회에 쓰이는 DTO(엘라스틱 서치)
 */
@Getter
@SuperBuilder
public class ProjectEsListDTO {
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

    @Schema(description = "프로젝트 모집 분야")
    private List<PositionResponseDTO> positions;

    @Schema(description = "프로젝트 기술 스택")
    private List<TechStackResponseDTO> techStacks;

    @Schema(description = "프로젝트 모집 시작일")
    private LocalDate recruitmentStartDate;

    @Schema(description = "프로젝트 모집 마감일")
    private LocalDate recruitmentEndDate;

    @Schema(description = "예상 모집 인원")
    private int expectedMembers;

    public static ProjectEsListDTO toProjectEsDTO(ProjectEsDocument document){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return ProjectEsListDTO.builder()
                .projectPk(document.getProjectPk())
                .projectProfilePk(document.getProjectProfilePk())
                .userPk(document.getUserPk())
                .nickname(document.getNickname())
                .projectProfileTitle(document.getProjectProfileTitle())
                .projectStatus(document.getProjectStatus())
                .positions(convertPositions(document.getPositions()))  // List 변환
                .techStacks(convertTechStacks(document.getTechStacks()))  // List 변환
                .recruitmentStartDate(document.getRecruitmentStartDate())
                .recruitmentEndDate(document.getRecruitmentEndDate())
                .build();
    }

    // Position 변환 헬퍼 메서드
    private static List<PositionResponseDTO> convertPositions(List<PositionResponseDTO> positions) {
        if (positions == null) {
            return Collections.emptyList();
        }
        return positions.stream()
                .map(pos -> PositionResponseDTO.builder()
                        .positionPk(pos.getPositionPk())
                        .positionName(pos.getPositionName())
                        .build())
                .collect(Collectors.toList());
    }

    // TechStack 변환 헬퍼 메서드
    private static List<TechStackResponseDTO> convertTechStacks(List<TechStackResponseDTO> techStacks) {
        if (techStacks == null) {
            return Collections.emptyList();
        }
        return techStacks.stream()
                .map(tech -> TechStackResponseDTO.builder()
                        .techPk(tech.getTechPk())
                        .techName(tech.getTechName())
                        .build())
                .collect(Collectors.toList());
    }
}