package com.jvc.studyroom.domain.user.service;

import com.jvc.studyroom.domain.user.security.CustomUserDetails;

import com.jvc.studyroom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    // Username을 사용해야하지만 email로 조회
    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("사용자 없음: " + email)))
                .map(CustomUserDetails::new);
    }
}