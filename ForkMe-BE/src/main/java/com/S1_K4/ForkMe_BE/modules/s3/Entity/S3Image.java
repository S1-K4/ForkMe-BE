package com.S1_K4.ForkMe_BE.modules.s3.Entity;

import com.S1_K4.ForkMe_BE.modules.on_project.board.entity.BoardInProject;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.s3.Entity
 * @fileName : S3Image
 * @date : 2025-08-07
 * @description : S3Image 엔티티
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
}