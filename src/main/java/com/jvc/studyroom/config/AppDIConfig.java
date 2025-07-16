package com.jvc.studyroom.config;

import com.jvc.studyroom.domain.user.AuthTokenFilter;
import com.jvc.studyroom.domain.user.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppDIConfig {
    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    public CurrentUserArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentResolver();
    }

}
