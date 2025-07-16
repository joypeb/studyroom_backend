package com.jvc.studyroom.domain.studySession.controller;

import com.jvc.studyroom.domain.studySession.dto.SessionCreateRequest;
import com.jvc.studyroom.domain.studySession.dto.StudySessionCreateResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionListResponse;
import com.jvc.studyroom.domain.studySession.dto.StudySessionResponse;
import com.jvc.studyroom.domain.studySession.entity.StudySession;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import com.jvc.studyroom.domain.user.CurrentUser;
import com.jvc.studyroom.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/*

학습 세션 일시정지				PATCH	/study/sessions/start/{sessionId}/pause
학습 세션 종료				PATCH	/study/sessions/start/{sessionId}/end

특정 학생 세션 기록				GET	/study/sessions/{studentId}/sessions
특정 학생의 현재 세션				GET	/study/sessions/{studentId}/sessions/current
 */
@RestController
@RequestMapping("study/sessions")
@RequiredArgsConstructor
public class StudySessionControllerV1 {
    private final StudySessionService service;

    // 현재 학습 세션 목록
    @GetMapping
    public Flux<StudySessionListResponse> getSessionList(@RequestParam(defaultValue = "createdAt,desc") String sort) {
        Sort sortObj = Sort.by(
                Sort.Order.desc("createdAt")
        );
        return service.getSessionList(sortObj);
    }

    //특정 학습 세션 상세 정보
    @GetMapping("/{sessionId}")
    public Mono<StudySessionResponse> getSession(@PathVariable UUID sessionId) {
        return service.getSession(sessionId);
    }
    // 학습 세션 생성
    @PostMapping("/new")
    public Mono<StudySessionCreateResponse> createSession(@RequestBody SessionCreateRequest request, @CurrentUser User loginUser) {
        return service.createSession(request, loginUser);
    }

    // 학습 세션 시작      @PostMapping("/start")

    // 학습 세션 재개
    @PatchMapping("/{sessoionId}/resume") // 이게 필요햔가?
    public ResponseEntity<Mono<Void>> resumeSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(service.resumeSession(sessionId));
    }
}
