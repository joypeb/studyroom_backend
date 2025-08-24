package com.jvc.studyroom.domain.user;

import com.jvc.studyroom.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class AuthTokenFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 임의 테스트용 User
        User mockUser = new User();
        mockUser.setUserId(UUID.fromString("3f9aaeb4-d516-455b-bf76-d4ed6222d42e"));
        mockUser.setName("crystal");
        mockUser.setEmail("crystal@test.com");

        return chain.filter(exchange)
                .contextWrite(UserContext.withUser(mockUser));
    }

}
