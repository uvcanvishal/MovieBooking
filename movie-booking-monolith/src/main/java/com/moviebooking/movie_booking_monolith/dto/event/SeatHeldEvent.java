package com.moviebooking.movie_booking_monolith.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor  // Jackson default constructor
@AllArgsConstructor  // Uses this for deserialization
public class SeatHeldEvent {
    private String showId;
    private Long seatId;
    private String userId;
    private LocalDateTime holdUntil;
}
