package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentCallbackRequest(
        @NotNull Long bookingId,
        @NotBlank String gatewayPaymentId,
        @NotBlank String gatewayOrderId,
        @NotBlank String signature,     // for real Razorpay verification later
        boolean success
) {}
