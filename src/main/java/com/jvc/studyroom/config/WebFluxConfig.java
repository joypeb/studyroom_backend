package com.jvc.studyroom.config;

import com.jvc.studyroom.domain.user.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {
    private final CurrentUserArgumentResolver currentUserArgumentResolver;
    public WebFluxConfig(CurrentUserArgumentResolver currentUserArgumentResolver) {
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }
    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(currentUserArgumentResolver);
    }

}
