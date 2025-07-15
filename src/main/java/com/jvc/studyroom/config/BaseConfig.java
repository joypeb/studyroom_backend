package com.jvc.studyroom.config;

import com.jvc.studyroom.domain.user.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {
    @Bean
    public AuthTokenFilter AuthTokenFilter() {
        return new AuthTokenFilter();
    }

}
