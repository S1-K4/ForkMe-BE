package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.user.dto.SideBarResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserInfoResponseDto;
import com.S1_K4.ForkMe_BE.modules.user.dto.UserProfile;
import com.S1_K4.ForkMe_BE.modules.user.entity.User;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.service
 * @fileName : UserService
 * @date : 2025-08-06
 * @description : 유저 서비스
 */
public interface UserService {

    UserInfoResponseDto getMyProfile(Long userPk);

    UserProfile getUserProfile(Long userPk);

    SideBarResponseDto getSidebarInfo(Long userPk);

    void updateUserTechStack(User user, List<Long> techStackPkList);
}
