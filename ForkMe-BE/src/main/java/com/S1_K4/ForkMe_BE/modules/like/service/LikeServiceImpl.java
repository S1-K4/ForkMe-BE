package com.S1_K4.ForkMe_BE.modules.like.service;

import com.S1_K4.ForkMe_BE.global.exception.CustomException;
import com.S1_K4.ForkMe_BE.modules.like.dto.LikeDTO;
import com.S1_K4.ForkMe_BE.modules.like.entity.Likes;
import com.S1_K4.ForkMe_BE.modules.like.repository.LikeRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.ProjectProfile;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectProfileRepository;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.modules.like.service
 * @fileName : LikeServiceImpl
 * @date : 2025-08-11
 * @description : LikeServiceImpl
 */
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService{
    private final UserRepository userRepository;
    private final ProjectProfileRepository projectProfileRepository;
    private final LikeRepository likeRepository;

    /**
    * 특정 profile의 좋아요 유무 확인하는 메서드
    * */
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikeProfile(Long userPk, Long profilePk){
        return likeRepository.existsByUserAndProjectProfile(userPk, profilePk);
    }

    /**
     * 해당 프로젝트의 좋아요 수 카운트하는 메서드
     * */
    @Override
    @Transactional(readOnly = true)
    public Long countLike(Long profilePk){
        projectProfileRepository.findById(profilePk).orElseThrow(()
                -> new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));
        return likeRepository.countByProjectProfile_ProjectProfilePk(profilePk);

    }

    /**
     * 특정 profile에 좋아요 추가하는 메서드
     * */
    @Override
    @Transactional
    public LikeDTO createLike(Long userPk, Long profilePk){
        User user = userRepository.findById(userPk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        ProjectProfile profile = projectProfileRepository.findById(profilePk)
                .orElseThrow(()->new CustomException(CustomException.ErrorCode.PROJECT_NOT_FOUND));

        if(likeRepository.existsByUserAndProjectProfile(userPk, profilePk)){
            throw new CustomException(CustomException.ErrorCode.ALREDAY_LIKED);
        }

        Likes like = Likes.builder()
                .user(user)
                .projectProfile(profile)
                .build();

        likeRepository.save(like);
        return LikeDTO.builder()
                .isLiked(true)
                .likeCount(likeRepository.countByProjectProfile(profile))
                .build();
    }

    /**
     * 좋아요 삭제 메서드
     * */
    @Override
    @Transactional
    public LikeDTO deleteLike(Long userPk, Long profilePk){
        userRepository.findById(userPk).orElseThrow(()->new CustomException(CustomException.ErrorCode.USER_NOT_FOUND));

        Likes like = likeRepository
                .findByUser_UserPkAndProjectProfile_ProjectProfilePk(userPk, profilePk)
                .orElseThrow(() -> new CustomException(CustomException.ErrorCode.LIKED_NOT_FOUND));

        likeRepository.delete(like);

        return LikeDTO.builder()
                .isLiked(false)
                .likeCount(likeRepository.countByProjectProfile_ProjectProfilePk(profilePk))
                .build();
    }

}