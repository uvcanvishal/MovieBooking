package com.moviebooking.movie_booking_monolith.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SeatLockResponse(
        List<String> lockedSeatNumbers,
        LocalDateTime lockExpiresAt,
        String message
) {}
