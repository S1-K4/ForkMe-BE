package com.S1_K4.ForkMe_BE.modules.mypage.service;

import com.S1_K4.ForkMe_BE.modules.mypage.dto.MyPageResponseDto;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.mypage.service
 * @fileName : MyPageService
 * @date : 2025-08-07
 * @description : 마이페이지 서비스
 */
public interface MyPageService {

    MyPageResponseDto getMyPage(Long userPk);

}
