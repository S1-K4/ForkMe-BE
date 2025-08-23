package com.S1_K4.ForkMe_BE.modules.on_project.webhook.service;

import com.S1_K4.ForkMe_BE.modules.on_project.webhook.EventGroup;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.Webhook;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.dto.GithubEventResponseDto;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.repository.WebhookRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.service
 * @fileName : WebhookService
 * @date : 2025-08-08
 * @description : 깃허브 웹훅 서비스 입니다.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final ProjectRepository projectRepository;

    public void recordTimeline(Long projectPk, String eventType, String deliveryId, JsonNode p) {
        if(webhookRepository.findByDeliveryId(deliveryId).isPresent()){
            return;
        }

        EventGroup group = toGroup(eventType);
        String summary = summarize(eventType, p);

        Project project = null;
        if (projectPk != null) {
            // DB hit 없이 프록시로 참조만 얻고 싶으면 getReferenceById 사용 (Spring Data JPA 2.7+/JPA 2.1+)
            project = projectRepository.getReferenceById(projectPk);
        }

        Long repoId = optLong(p.at("/repository/id"));
        String repoFullName = optText(p.at("/repository/full_name"));
        String orgLogin = optText(p.at("/organization/login"));
        String actorLogin = optText(p.at("/sender/login"));
        String actorAvatar = optText(p.at("/sender/avatar_url"));

        Webhook row = Webhook.builder()
                .project(project)
                .eventType(eventType)
                .eventGroup(group)
                .eventSummary(summary)
                .deliveryId(deliveryId)
                .actorLogin(actorLogin)
                .actorAvatar(actorAvatar)
                .repositoryId(repoId)
                .repositoryFullName(repoFullName)
                .organizationLogin(orgLogin)
                .build();

        try{
            webhookRepository.save(row);
        } catch (DataIntegrityViolationException e) {
            log.warn("duplicate delivery ID : {}",deliveryId);
        }
    }

    private static EventGroup toGroup(String event) {
        return switch (event) {
            case "push" -> EventGroup.PUSH;
            case "pull_request", "pull_request_review", "pull_request_review_comment", "merge_group" -> EventGroup.PULL_REQUEST;
            case "issues" -> EventGroup.ISSUES;
            case "issue_comment" -> EventGroup.ISSUE_COMMENT;
            case "release" -> EventGroup.RELEASE;
            case "create", "delete" -> EventGroup.CREATE_DELETE;
            default -> EventGroup.OTHERS;
        };
    }

    private static String summarize(String event, JsonNode p ) {
        try {
            return switch (event) {
                case "push" -> {
                    String ref = optText(p.get("ref"));
                    int commits = p.has("commits") && p.get("commits").isArray() ? p.get("commits").size() : 0;
                    yield "push " + commits + " commits to " + shortRef(ref);
                }
                case "pull_request" -> {
                    String action = optText(p.get("action"));
                    int number = p.at("/pull_request/number").asInt();
                    String title = optText(p.at("/pull_request/title"));
                    yield "PR #" + number + " " + action + " - " + title;
                }
                case "issues" -> {
                    String action = optText(p.get("action"));
                    int number = p.at("/issue/number").asInt();
                    String title = optText(p.at("/issue/title"));
                    yield "Issue #" + number + " " + action + " - " + title;
                }
                case "issue_comment" -> {
                    String action = optText(p.get("action"));
                    int number = p.at("/issue/number").asInt();
                    yield "Issue #" + number + " comment " + action;
                }
                case "release" -> {
                    String action = optText(p.get("action"));
                    String tag = optText(p.at("/release/tag_name"));
                    yield "Release " + action + " - " + tag;
                }
                case "create", "delete" -> {
                    String refType = optText(p.get("ref_type"));
                    String ref = optText(p.get("ref"));
                    yield event + " " + refType + " " + ref;
                }
                default -> event;
            };
        } catch (Exception e) {
            return event;
        }
    }

    private static String shortRef(String ref) {
        if(ref == null) return "";
        int i = ref.lastIndexOf('/');
        return (i >= 0) ? ref.substring(i + 1) : ref;
    }

    private static String optText(JsonNode n) { return (n == null || n.isMissingNode() || n.isNull()) ? null : n.asText(); }
    private static Long optLong(JsonNode n) { return (n == null || n.isMissingNode() || n.isNull()) ? null : n.asLong(); }

    // 페이징으로 이벤트 목록 가져오기
    @Transactional(readOnly = true)
    public List<GithubEventResponseDto> getEventsByProject(Long projectPk, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("createdAt").descending());
        return webhookRepository.findByProject_ProjectPkOrderByCreatedAtDesc(projectPk, pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 간단한 연결 여부 체크
    @Transactional(readOnly = true)
    public boolean isWebhookConnected(Long projectPk) {
        return webhookRepository.existsByProject_ProjectPk(projectPk);
    }

    // 엔티티 -> DTO 변환 헬퍼 (필드명이 다르면 getter 이름을 맞춰주세요)
    private GithubEventResponseDto toDto(Webhook w) {
        if (w == null) return null;

        // project PK 안전 추출
        Long projPk = null;
        if (w.getProject() != null) {
            try { projPk = w.getProject().getProjectPk(); } catch (Exception ignored) {}
        }

        // actorUser PK 안전 추출 (actorUser는 User 엔티티이고 PK는 getUserPk())
        Long actorUserPk = null;
        if (w.getActorUser() != null) {
            try { actorUserPk = w.getActorUser().getUserPk(); } catch (Exception ignored) {}
        }

        String eventGroupStr = (w.getEventGroup() != null) ? w.getEventGroup().name() : null;

        return GithubEventResponseDto.builder()
                .gitTimelinePk(w.getGitTimelinePk())
                .projectPk(projPk)
                .eventType(w.getEventType())
                .eventGroup(eventGroupStr)
                .eventSummary(w.getEventSummary())
                .deliveryId(w.getDeliveryId())
                .actorUserPk(actorUserPk)
                .actorLogin(w.getActorLogin())
                .actorAvatar(w.getActorAvatar())
                .repositoryId(w.getRepositoryId())
                .repositoryFullName(w.getRepositoryFullName())
                .organizationLogin(w.getOrganizationLogin())
                .createdAt(w.getCreatedAt())
                .build();
    }
}
