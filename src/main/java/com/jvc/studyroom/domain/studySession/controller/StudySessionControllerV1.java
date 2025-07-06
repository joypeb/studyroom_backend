package com.jvc.studyroom.domain.studySession.controller;

import com.jvc.studyroom.domain.studySession.dto.StudySessionList;
import com.jvc.studyroom.domain.studySession.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/*
학습 세션 시작				POST	/study/sessions/start
학습 세션 일시정지				PATCH	/study/sessions/start/{sessionId}/pause
학습 세션 재개				PATCH	/study/sessions/start/{sessionId}/resume
학습 세션 종료				PATCH	/study/sessions/start/{sessionId}/end
현재 학습 세션 목록				GET	/study/sessions
특정 학습 세션 상세 정보				GET	/study/sessions/{sessionId}
특정 학생 세션 기록				GET	/study/sessions/{studentId}/sessions
특정 학생의 현재 세션				GET	/study/sessions/{studentId}/sessions/current
 */
@ResponseBody
@RequestMapping("study/sessions")
@RequiredArgsConstructor
public class StudySessionControllerV1 {
    private final StudySessionService service;

    // 현재 학습 세션 목록
    @GetMapping
    public ResponseEntity<Flux<StudySessionList>> getSessionList() {
        return ResponseEntity.ok(service.getSessionList());
    }
}
