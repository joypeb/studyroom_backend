package com.jvc.studyroom.domain.monitoring.controller;


import com.jvc.studyroom.domain.monitoring.dto.StudySessionMonitoringResponse;
import com.jvc.studyroom.domain.monitoring.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 독서실 좌석 실시간 모니터링 컨트롤러
 * - 실시간 좌석 현황 스트리밍
 * - 개별 좌석 상세 정보
 */
@RestController
@RequestMapping("admin/monitoring")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 프론트엔드에서 접근 가능하도록
@Slf4j
public class MonitoringController {

    private final MonitoringService monitoringService;

    /**
     * 실시간 세션 현황 스트리밍 (Server-Sent Events)
     * 5초마다 전체 활성 세션 정보 전송
     */
    @GetMapping(value = "/sessions/realtime", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StudySessionMonitoringResponse>> getRealTimeSessionsStatus() {
        return Flux.interval(Duration.ofSeconds(5))
                .onBackpressureLatest()
                .flatMap(tick -> monitoringService.getAllActiveSessionsStatus()
                        .map(data -> ServerSentEvent.<StudySessionMonitoringResponse>builder()
                                .id(String.valueOf(tick))
                                .event("sessions-update")
                                .data(data)
                                .comment("실시간 세션 현황")
                                .build())
                        .onErrorResume(error -> {
                            log.error("실시간 세션 데이터 조회 오류: ", error);
                            return Mono.just(ServerSentEvent.<StudySessionMonitoringResponse>builder()
                                    .id(String.valueOf(tick))
                                    .event("error")
                                    .data(StudySessionMonitoringResponse.empty())
                                    .comment("오류 발생")
                                    .build());
                        }))
                .distinctUntilChanged(sse -> sse.data())
                .doOnSubscribe(subscription -> log.info("실시간 세션 모니터링 클라이언트 연결"))
                .doOnCancel(() -> log.info("실시간 세션 모니터링 클라이언트 연결 해제"));
    }
}