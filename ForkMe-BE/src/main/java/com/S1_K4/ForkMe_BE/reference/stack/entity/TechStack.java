package com.S1_K4.ForkMe_BE.reference.stack.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.stack.entity
 * @fileName : TechStack
 * @date : 2025-08-04
 * @description : 기술 스택 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name="tech_stack")
@Builder
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tech_pk")
    private Long techPk;

    @Column(name="tech_name")
    private String techName;
}