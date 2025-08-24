package com.jvc.studyroom.domain.monitoring.controller;

import com.jvc.studyroom.common.dto.ApiResponse;
import com.jvc.studyroom.domain.monitoring.dto.SessionMonitoringData;
import com.jvc.studyroom.domain.monitoring.dto.SessionSummaryData;
import com.jvc.studyroom.domain.monitoring.service.MonitoringService;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 독서실 실시간 모니터링 컨트롤러 - 요악/상세 버전이 따로 있음.
 */
@RestController
@RequestMapping("admin/monitoring")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class MonitoringController {

  private final MonitoringService monitoringService;

  // ================================
  // 목록형 API (요약)
  // ================================

  /**
   * 실시간 활성 세션 목록 스트리밍 (요약)
   */
  @GetMapping(value = "/sessions/list/realtime", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<List<SessionSummaryData>>> getRealTimeSessionsList() {
    return Flux.interval(Duration.ofSeconds(10)) // 10초마다 업데이트
        .onBackpressureLatest()
        .flatMap(tick -> monitoringService.getTodaySessionsSummary()
            .collectList()
            .map(sessionList -> ServerSentEvent.<List<SessionSummaryData>>builder()
                .id(String.valueOf(tick))
                .event("sessions-list-update")
                .data(sessionList)
                .comment("실시간 활성 세션 목록 업데이트")
                .build())
            .onErrorResume(error -> {
              log.error("실시간 세션 목록 조회 오류: ", error);
              return Mono.just(ServerSentEvent.<List<SessionSummaryData>>builder()
                  .id(String.valueOf(tick))
                  .event("error")
                  .data(List.of())
                  .comment("오류 발생")
                  .build());
            }))
        .distinctUntilChanged(ServerSentEvent::data)
        .doOnSubscribe(subscription -> log.info("실시간 세션 목록 클라이언트 연결"))
        .doOnCancel(() -> log.info("실시간 세션 목록 클라이언트 연결 해제"));
  }

  /**
   * 당일 활성 세션 목록 스냅샷 (요약)
   */
  @GetMapping("/sessions/list")
  public Mono<ApiResponse<List<SessionSummaryData>>> getTodaySessionsList() {
    return monitoringService.getTodaySessionsSummary()
        .collectList()
        .map(sessionList -> ApiResponse.success(
            String.format("당일 활성 세션 목록 %d개 조회 성공", sessionList.size()),
            sessionList))
        .onErrorResume(error -> {
          log.error("당일 세션 목록 조회 오류: ", error);
          return Mono.just(ApiResponse.error("세션 목록 조회 중 오류가 발생했습니다: " + error.getMessage()));
        });
  }

  // ================================
  // 상세용 API (전체 정보)
  // ================================

  /**
   * 실시간 활성 세션 상세 스트리밍 (전체 상세 정보)
   */
  @GetMapping(value = "/sessions/details/realtime", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<List<SessionMonitoringData>>> getRealTimeSessionsDetails() {
    return Flux.interval(Duration.ofSeconds(10))
        .onBackpressureLatest()
        .flatMap(tick -> monitoringService.getAllTodaySessions()
            .collectList()
            .map(sessionList -> ServerSentEvent.<List<SessionMonitoringData>>builder()
                .id(String.valueOf(tick))
                .event("sessions-details-update")
                .data(sessionList)
                .comment("실시간 활성 세션 상세 업데이트")
                .build())
            .onErrorResume(error -> {
              log.error("실시간 세션 상세 조회 오류: ", error);
              return Mono.just(ServerSentEvent.<List<SessionMonitoringData>>builder()
                  .id(String.valueOf(tick))
                  .event("error")
                  .data(List.of())
                  .comment("오류 발생")
                  .build());
            }))
        .distinctUntilChanged(ServerSentEvent::data)
        .doOnSubscribe(subscription -> log.info("실시간 세션 상세 클라이언트 연결"))
        .doOnCancel(() -> log.info("실시간 세션 상세 클라이언트 연결 해제"));
  }

  /**
   * 🔍 당일 활성 세션 상세 스냅샷 (전체 상세 정보)
   */
  @GetMapping("/sessions/details")
  public Mono<ApiResponse<List<SessionMonitoringData>>> getTodaySessionsDetails() {
    return monitoringService.getAllTodaySessions()
        .collectList()
        .map(sessionList -> ApiResponse.success(
            String.format("당일 활성 세션 상세 %d개 조회 성공", sessionList.size()),
            sessionList))
        .onErrorResume(error -> {
          log.error("당일 세션 상세 조회 오류: ", error);
          return Mono.just(ApiResponse.error("세션 상세 조회 중 오류가 발생했습니다: " + error.getMessage()));
        });
  }

  // ================================
  // 통계용 API
  // ================================

  /**
   * 활성 세션 개수만 반환
   */
  @GetMapping("/sessions/count")
  public Mono<ApiResponse<Long>> getActiveSessionsCount() {
    return monitoringService.getAllTodaySessions()
        .count()
        .map(count -> ApiResponse.success("활성 세션 개수 조회 성공", count))
        .onErrorResume(error -> {
          log.error("활성 세션 개수 조회 오류: ", error);
          return Mono.just(ApiResponse.error("세션 개수 조회 중 오류가 발생했습니다: " + error.getMessage()));
        });
  }
}
