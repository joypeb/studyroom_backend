package com.jvc.studyroom.domain.seat;

import com.jvc.studyroom.common.utils.PageableUtil;
import com.jvc.studyroom.domain.seat.repository.SeatRepository;
import com.jvc.studyroom.domain.seat.service.DefaultSeatService;
import com.jvc.studyroom.domain.seat.service.SeatFindService;
import com.jvc.studyroom.domain.seat.service.SeatFindServiceV1;
import com.jvc.studyroom.domain.seat.service.SeatService;
import com.jvc.studyroom.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeatConfig {

    @Bean
    public SeatFindService seatfindService(SeatRepository seatRepository) {
        return new SeatFindServiceV1(seatRepository);
    }

    @Bean
    public SeatService seatService(SeatRepository seatRepository,
                                   UserRepository userRepository,
                                   PageableUtil pageableUtil) {
        return new DefaultSeatService(seatRepository,userRepository,pageableUtil);
    }

    @Bean
    public PageableUtil pageableUtil() {
        return new PageableUtil();
    }

}
