package com.S1_K4.ForkMe_BE.modules.project.dto;

import com.S1_K4.ForkMe_BE.modules.project.enums.IsLeader;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.project.dto
 * @fileName : ProjectMemberListDTO
 * @date : 2025-08-20
 * @description : 프로젝트 참여자 목록 DTO
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMemberListDTO {
    private Long userPk;
    private String profile_url;
    private String nickname;
    private String isLeader;
}