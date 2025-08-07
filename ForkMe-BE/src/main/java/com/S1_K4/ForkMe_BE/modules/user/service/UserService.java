package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.user.dto.SidebarResponseDto;
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

    public SidebarResponseDto getSidebarInfo(User user);

    public void updateUserTechStack(User user, List<Long> techStackPkList);
}
