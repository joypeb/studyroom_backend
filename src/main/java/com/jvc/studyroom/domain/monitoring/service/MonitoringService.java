package com.jvc.studyroom.domain.monitoring.service;

import com.jvc.studyroom.domain.monitoring.dto.StudySessionMonitoringResponse;
import reactor.core.publisher.Mono;

public interface MonitoringService {
    Mono<StudySessionMonitoringResponse> getAllActiveSessionsStatus();
}
