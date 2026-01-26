package com.moviebooking.movie_booking_monolith.dto.response;

public record PaymentInitResponse(
        Long bookingId,
        Double amount,
        String currency,
        String gatewayOrderId   // for Razorpay frontend
) {}
