package com.jvc.studyroom.config;

import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.common.enums.AccountStatus;
import com.jvc.studyroom.common.enums.UserRole;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 테스트용 설정 클래ス
 * @CurrentUser 어노테이션을 처리하기 위한 Mock ArgumentResolver 제공
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public HandlerMethodArgumentResolver testCurrentUserArgumentResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(com.jvc.studyroom.domain.user.CurrentUser.class)
                        && User.class.isAssignableFrom(parameter.getParameterType());
            }

            @Override
            public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, 
                                              ServerWebExchange exchange) {
                // 테스트용 Mock User 생성
                User testUser = new User();
                testUser.setUserId(UUID.randomUUID());
                testUser.setEmail("test@example.com");
                testUser.setName("테스트 사용자");
                testUser.setUsername("testuser");
                testUser.setRole(UserRole.STUDENT);
                testUser.setAccountStatus(AccountStatus.ACTIVE);
                testUser.setCreatedAt(OffsetDateTime.now());
                testUser.setUpdatedAt(OffsetDateTime.now());
                
                return Mono.just(testUser);
            }
        };
    }
}
