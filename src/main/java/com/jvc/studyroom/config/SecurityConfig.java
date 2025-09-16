package com.jvc.studyroom.config;

import com.jvc.studyroom.domain.user.jwt.JwtAuthenticationFilter;
import com.jvc.studyroom.domain.user.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
//                        .anyExchange().permitAll()    // 모든 권한 허용
//                        .pathMatchers("/user/**").hasRole("USER")   // USER 역할만 접근 가능 이렇게 역할마다 정할 수 있음
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/oauth2/authorization/kakao", "/login/oauth2/code/kakao").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(oauth2LoginSuccessHandler)
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}