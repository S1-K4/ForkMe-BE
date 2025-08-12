package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.service
 * @fileName : ApplyService
 * @date : 2025-08-11
 * @description : 신청서 service
 */
public interface ApplyService {

    /*
    지원서 리스트
     */
    List<ApplyDto> getApplyList(Long userPk, List<String> stateList);
}
