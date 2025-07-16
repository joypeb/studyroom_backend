package com.jvc.studyroom.domain.user;

import com.jvc.studyroom.domain.user.model.User;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class UserContext {
    public static final String CONTEXT_KEY = "currentUser";

    public static Context withUser(User user) {
        return Context.of(CONTEXT_KEY, user);
    }

    public static Mono<Object> getUser() {
        return Mono.deferContextual(ctx -> Mono.just(ctx.get(CONTEXT_KEY)));
    }
}
