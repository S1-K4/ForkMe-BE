package com.S1_K4.ForkMe_BE.modules.comment.entity;

import com.S1_K4.ForkMe_BE.global.common.entity.BaseTime;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.comment.entity
 * @fileName : Comment
 * @date : 2025-08-07
 * @description : comment entity
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_pk")
    private Long commentPk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_pk", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_profile_pk", nullable = false)
    private ProjectProfile projectProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pk")
    private Comment parent;

    private String comment;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder Comment(User user, ProjectProfile projectProfile, Comment parent, String comment){
        this.user = user;
        this.projectProfile = projectProfile;
        this.parent = parent;
        this.comment = comment;
    }



    public void updateComment(String comment) {
        this.comment = comment;
    }

}
