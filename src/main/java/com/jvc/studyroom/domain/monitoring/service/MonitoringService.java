package com.jvc.studyroom.domain.monitoring.service;

import com.jvc.studyroom.domain.monitoring.dto.SessionMonitoringData;
import com.jvc.studyroom.domain.monitoring.dto.SessionSummaryData;
import reactor.core.publisher.Flux;

public interface MonitoringService {

  Flux<SessionMonitoringData> getAllTodaySessions();

  /**
   * 당일 활성 세션 상세 정보 (상세 페이지용)
   */
  //Flux<SessionMonitoringData> getAllTodaySessionsStatus();

  /**
   * 당일 활성 세션 요약 정보 (목록용)
   */
  Flux<SessionSummaryData> getTodaySessionsSummary();

}
