package com.moviebooking.movie_booking_monolith.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long bookingId;
    private Long userId;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String status;
    private Double amount;
    private Instant eventTime;
    private String failureReason;
}
