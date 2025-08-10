package com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto
 * @fileName : PendingHookRequest
 * @date : 2025-08-10
 * @description : 세션에 훅 정보를 담아두는 DTO 입니다
 */
public record PendingHookRequest(
        String mode, //repo or Org
        String owner,
        String repo,
        List<String> events,
        Boolean insecureSsl,
        String overrideSecret
) implements Serializable {
}
