package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.NotNull;

public record PaymentInitRequest(
        @NotNull Long bookingId
) {}
