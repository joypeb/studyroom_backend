package com.jvc.studyroom.domain.seat;

import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.seat.service.SeatFindServiceV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeatConfig {

    @Bean
    public SeatFindService seatfindService(SeatRepository seatRepository) {
        return new SeatFindServiceV1(seatRepository);
    }

}
