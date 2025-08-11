package com.S1_K4.ForkMe_BE.global.common.entity;

import com.S1_K4.ForkMe_BE.global.common.common_enum.Yn;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.common.entity
 * @fileName : BaseTime
 * @date : 2025-08-03
 * @description :
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
//이 클래스를 상속받는 엔티티들은 이 클래스의 필드를 컬럼으로 포함시켜라는 어노테이션
@MappedSuperclass
public abstract class BaseTime {

    //엔티티가 저장될때 자동으로 시간을 기록
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATETIME",
            updatable = false, nullable = false)
    private LocalDateTime createdAt;

    //수정될 때마다 기록
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATETIME",
            nullable = false)
    private LocalDateTime updatedAt;

    //삭제 처리될 때마다 기록
    @Column(name = "deleted_yn",columnDefinition = "CHAR(1) NOT NULL DEFAULT 'N'")
    @Enumerated(EnumType.STRING)
    private Yn deletedYN = Yn.N;


    //삭제 처리 메서드
    public void markDeleted() {
        this.deletedYN = Yn.Y;
    }
}