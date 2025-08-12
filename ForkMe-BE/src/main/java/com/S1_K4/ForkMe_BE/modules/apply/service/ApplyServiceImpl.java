package com.S1_K4.ForkMe_BE.modules.apply.service;

import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyDto;
import com.S1_K4.ForkMe_BE.modules.apply.dto.ApplyTechStackDto;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyRepository;
import com.S1_K4.ForkMe_BE.modules.apply.repository.ApplyTechStackRepository;
import com.S1_K4.ForkMe_BE.reference.stack.dto.TechStackResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 김종국
 * @packageName : com.S1_K4.ForkMe_BE.modules.apply.service
 * @fileName : ApplyServiceImpl
 * @date : 2025-08-11
 * @description : 신청서 service
 */
@Service
@AllArgsConstructor
public class ApplyServiceImpl implements ApplyService {

    private final ApplyRepository applyRepository;
    private final ApplyTechStackRepository applyTechStackRepository;

    @Override
    public List<ApplyDto> getApplyList(Long userPk, List<String> statusList) {

        List<ApplyDto> applyList = applyRepository.findApplyByUserPkInState(userPk, statusList);

        List<Long> applyPkList = applyList.stream().map(ApplyDto::getApplyPk).toList();

        List<ApplyTechStackDto> applyTechStackList = applyTechStackRepository.findApplyTechStacksByApplyPkIn(applyPkList);
        Map<Long, List<TechStackResponseDTO>> applyTechStackMap = applyTechStackList.stream()
                .collect(Collectors.groupingBy(
                        ApplyTechStackDto::getApplyPk,
                        Collectors.mapping(
                                dto -> new TechStackResponseDTO(dto.getTechPk(), dto.getTechName()),
                                Collectors.toList()
                        )
                ));

        for (ApplyDto applyDto : applyList) {
            applyDto.setTechStacks(applyTechStackMap.get(applyDto.getApplyPk()));
        }

        return applyList;
    }


}