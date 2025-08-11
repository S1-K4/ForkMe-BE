package com.S1_K4.ForkMe_BE.modules.on_project.webhook.service;

import com.S1_K4.ForkMe_BE.modules.on_project.webhook.EventGroup;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.Webhook;
import com.S1_K4.ForkMe_BE.modules.on_project.webhook.repository.WebhookRepository;
import com.S1_K4.ForkMe_BE.modules.project.entity.Project;
import com.S1_K4.ForkMe_BE.modules.project.repository.ProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

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

}
