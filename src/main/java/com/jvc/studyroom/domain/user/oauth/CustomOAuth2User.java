package com.jvc.studyroom.domain.user.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String id;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String email;

    public CustomOAuth2User(String id, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities, String name, String email) {
        this.id = id;
        this.attributes = attributes;
        this.authorities = authorities;
        this.name = name;
        this.email = email;
    }

    @Override
    public <A> A getAttribute(String name) {
        return (A) attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        return this.name;
    }
}