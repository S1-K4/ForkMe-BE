package com.S1_K4.ForkMe_BE.modules.s3.entity;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 김송이
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.entity
 * @fileName : S3File
 * @date : 2025-08-08
 * @description : S3File 엔티티 클래스입니다.
 */

@Table(name = "s3_file")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
@Entity
public class S3File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_pk")
    private Long filePk;

    @Column(name = "url", length = 255, nullable = false)
    private String url;


    @Column(name = "original_file_name", length = 255)
    private String originalFileName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_in_project_pk", referencedColumnName = "board_in_project_pk")
    private BoardInProject boardInProject;

    // 정적 팩토리 메서드 수정
    public static S3File create(String url, String originalFileName, BoardInProject boardInProject) {
        return S3File.builder()
                .url(url)
                .originalFileName(originalFileName)
                .boardInProject(boardInProject)
                .build();
    }
}