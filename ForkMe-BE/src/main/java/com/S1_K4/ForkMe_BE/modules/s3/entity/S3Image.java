package com.S1_K4.ForkMe_BE.modules.s3.entity;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import jakarta.persistence.*;
import lombok.*;


/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.entity
 * @fileName : S3Image
 * @date : 2025-08-08
 * @description : S3Image Entity 입니다
 */

@Entity
@Table(name = "s3_image")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class S3Image {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s3_image_pk")
    private Long s3ImagePk;

    @Column(name = "url", length = 255)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_profile_pk", nullable = false)
    private ProjectProfile projectProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_in_project_pk")
    private BoardInProject boardInProject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_pk")
    private Project project;


    // 정적 팩토리 메서드 추가
    public static S3Image create(String url, BoardInProject boardInProject) {
        return S3Image.builder()
                .url(url)
                .boardInProject(boardInProject)
                .project(boardInProject.getProject()) // 이게 가능하다면
                .build();
    }


}