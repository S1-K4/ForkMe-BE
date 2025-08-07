package com.S1_K4.ForkMe_BE.modules.user.dto;

import com.S1_K4.ForkMe_BE.modules.project.dto.PreparingProjectDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.dto
 * @fileName : SidebarResponseDto
 * @date : 2025-08-07
 * @description : 사이드바 응답 dto
 */
@AllArgsConstructor
@Builder
@Getter
public class SidebarResponseDto {

    private List<PreparingProjectDto> preparingProjectList;
    private List<PreparingProjectDto> workSpaceList;

}