package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SeatLockRequest(
        @NotNull Long showId,
        @NotEmpty List<String> seatNumbers,
        @NotNull @Min(1) @Max(30) Integer lockDurationMinutes
) {}
