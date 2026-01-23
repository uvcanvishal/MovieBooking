package com.moviebooking.movie_booking_monolith.config;

import com.moviebooking.movie_booking_monolith.service.SeatLockService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SeatLockSchedulerConfig {

    private final SeatLockService seatLockService;

    public SeatLockSchedulerConfig(SeatLockService seatLockService) {
        this.seatLockService = seatLockService;
    }

    @Scheduled(fixedDelay = 60_000)  // Every 1 minute
    public void releaseExpiredLocks() {
        seatLockService.releaseExpiredLocks();
    }
}
