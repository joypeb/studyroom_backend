package com.jvc.studyroom.domain.studySession.controller;

import com.jvc.studyroom.domain.studySession.dto.*;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import com.jvc.studyroom.domain.user.CurrentUser;
import com.jvc.studyroom.domain.user.model.User;
import com.jvc.studyroom.domain.user.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("study/sessions")
@RequiredArgsConstructor
public class StudySessionControllerV1 {
    private final StudySessionService service;

    // 전체 학습 세션 목록
    @GetMapping
    public Flux<StudySessionListResponse> getSessionList(@RequestParam(defaultValue = "createdAt,desc") String sort) {
        Sort sortObj = Sort.by(
                Sort.Order.desc("createdAt")
        );
        return service.getSessionList(sortObj);
    }
    // todo. 활성화 된 전체 학습 세션 목록

    //특정 학습 세션 상세 정보
    @GetMapping("/{sessionId}")
    public Mono<StudySessionResponse> getSession(@PathVariable UUID sessionId) {
        return service.getSession(sessionId);
    }
    // 학습 세션 생성
    @PostMapping("/new")
    public Mono<StudySessionCreateResponse> createSession(@RequestBody SessionCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return service.createSession(request, userDetails.getUser());
    }

    // 학습 세션 상태 변경 : 시작 | 재개 | 정지 등
    @PostMapping("/change-status")
    public Mono<Void> changeSessionStatus(@RequestBody SessionChangeStatusRequest request , @CurrentUser User loginUser) {
        return service.changeSessionStatus(request, loginUser);
    }
    //특정 학생 세션 전체 조회(관리자 기준)
    @GetMapping("/session-history/{studentId}")
    public Flux<SessionHistoryResponse> getSessionHistoryFromAdmin(@PathVariable UUID studentId) {
        return service.getSessionHistory(studentId);
    }

    //특정 학생 세션 전체 조회(로그인한 학생 기준)
    @GetMapping("/session-history")
    public Flux<SessionHistoryResponse> getSessionHistoryByLoginedStudent(@CurrentUser User loginUser) {
        return service.getSessionHistory(loginUser.getUserId());
    }
    //특정 학생의 현재 세션(관리자 기준)
    @GetMapping("/current/{studentId}") //Todo. 비즈니스 조건상 단일 조회일 것 같은데 우선  Flux로 설정
    public Flux<SessionHistoryResponse> getCurrentSessionByStudentFromAdmin(@PathVariable UUID studentId) {
        return service.getCurrentSession(studentId);
    }
    //특정 학생의 현재 세션(로그인한 학생 기준)
    @GetMapping("/current") //Todo. 비즈니스 조건상 단일 조회일 것 같은데 우선  Flux로 설정
    public Flux<SessionHistoryResponse> getCurrentSessionByStudent(@CurrentUser User loginUser) {
        return service.getCurrentSession(loginUser.getUserId());
    }
}
