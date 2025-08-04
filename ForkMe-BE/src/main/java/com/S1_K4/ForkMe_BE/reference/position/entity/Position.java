package com.S1_K4.ForkMe_BE.reference.position.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.reference.position.entity
 * @fileName : Position
 * @date : 2025-08-04
 * @description : position 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="position")
@Builder
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_pk")
    private Long positionPk;

    @Column(name ="position_name")
    private String positionName;

}