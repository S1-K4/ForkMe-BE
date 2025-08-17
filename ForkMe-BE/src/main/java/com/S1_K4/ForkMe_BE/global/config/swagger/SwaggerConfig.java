package com.S1_K4.ForkMe_BE.global.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.List;

/**
 * @author : 선순주
 * @packageName : com.S1_K4.ForkMe_BE.global.config.swagger
 * @fileName : SwaggerConfig
 * @date : 2025-08-03
 * @description : Swagger 관련 설정 파일입니다.
 * http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig<OpenApiCustomiser> {
    // API 메타정보
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ForkMe API")
                        .version("v1.0")
                        .description("ForkMe API 문서입니다."));
    }

    // JWT 보안 설정 커스터마이저
    private OpenApiCustomizer jwtSecurityCustomizer() {
        return openApi -> openApi.addSecurityItem(new SecurityRequirement().addList("jwt token"))
                .getComponents()
                .addSecuritySchemes("jwt token", new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .bearerFormat("JWT")
                        .scheme("bearer"));
    }

    // 소셜 로그인 API 그룹
    @Bean
    public GroupedOpenApi oauthApi() {
        return GroupedOpenApi.builder()
                .group("🌐 소셜 로그인 API")
                .pathsToMatch("/oauth2/docs/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 인증 API 그룹
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("🔐 인증 API")
                .pathsToMatch("/api/auth/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 사용자 API 그룹
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("👤 사용자 API")
                .pathsToMatch("/api/user/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 프로젝트 API 그룹
    @Bean
    public GroupedOpenApi projectApi() {
        return GroupedOpenApi.builder()
                .group("\uD83D\uDCBB 프로젝트 API")
                .pathsToMatch(
                        "/api/projects/**",
                        "/api/comments/**",
                        "/api/likes/**"
                )
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .pathsToExclude(
                        "/api/projects/*/applies/**",           //신청서 api 경로 제외
                        "/api/projects/*/member_review/**"      //멤버리뷰 api 경로 제외
                )
                .build();
    }

    // 신청서 API 그룹
    @Bean
    public GroupedOpenApi applyApi() {
        return GroupedOpenApi.builder()
                .group("\uD83D\uDCCB 신청서 API")
                .pathsToMatch("/api/projects/*/applies/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 채팅 API 그룹
    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                .group("💬 채팅 API")
                .pathsToMatch("/api/chat/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 게시판 API 그룹
    @Bean
    public GroupedOpenApi boardApi() {
        return GroupedOpenApi.builder()
                .group("📝 게시판 API")
                .pathsToMatch("/api/boards/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 일정 API 그룹
    @Bean
    public GroupedOpenApi scheduleApi() {
        return GroupedOpenApi.builder()
                .group("\uD83D\uDCC5 일정 API")
                .pathsToMatch("/api/schedule/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }

    // 깃 webhook 그룹
    @Bean
    public GroupedOpenApi webHookApi() {
        return GroupedOpenApi.builder()
                .group("\uD83D\uDC19 깃 webHook API")
                .pathsToMatch("/api/webHook/**")
                .addOpenApiCustomizer(jwtSecurityCustomizer())
                .build();
    }
}