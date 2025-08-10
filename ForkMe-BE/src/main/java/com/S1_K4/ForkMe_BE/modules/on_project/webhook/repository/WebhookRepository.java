package com.S1_K4.ForkMe_BE.modules.on_project.webhook.repository;

import com.S1_K4.ForkMe_BE.modules.on_project.webhook.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : 김관중
 * @packageName : com.S1_K4.ForkMe_BE.modules.on_project.webhook.repository
 * @fileName : webhookRepository
 * @date : 2025-08-07
 * @description : 깃허브 웹훅 리포지토리입니다.
 */
@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    Optional<Webhook> findByDeliveryId(String deliveryId);
}
