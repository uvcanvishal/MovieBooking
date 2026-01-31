package com.moviebooking.movie_booking_monolith.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private Long bookingId;
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
    private String status;
    private Double amount;
    private Instant eventTime;
    private String eventType;
}
