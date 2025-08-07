package com.S1_K4.ForkMe_BE.modules.user.service;

import com.S1_K4.ForkMe_BE.modules.user.entity.User;
import com.S1_K4.ForkMe_BE.modules.user.entity.UserTechStack;
import com.S1_K4.ForkMe_BE.modules.user.repository.UserTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.entity.TechStack;
import com.S1_K4.ForkMe_BE.reference.stack.repository.TechStackRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.user.service
 * @fileName : UserServiceImpl
 * @date : 2025-08-06
 * @description : 유저 서비스
 */

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserTechStackRepository userTechStackRepository;
    private final TechStackRepository techStackRepository;

    //@Override
    @Transactional
    public void updateUserTechStack(User user, List<Long> techStackList) {
        // 기존 기술 스택 삭제
        userTechStackRepository.deleteAllByUser(user);

        // 새로운 기술 스택 저장
        if (techStackList != null && !techStackList.isEmpty()) {
            List<TechStack> techStacks = techStackRepository.findAllById(techStackList);
            List<UserTechStack> userTechStacks = techStacks.stream()
                    .map(stack -> new UserTechStack(user, stack))
                    .toList();
            userTechStackRepository.saveAll(userTechStacks);
        }
    }


}