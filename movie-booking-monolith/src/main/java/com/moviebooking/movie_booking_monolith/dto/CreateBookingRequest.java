package com.moviebooking.movie_booking_monolith.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateBookingRequest {
    private Long userId;
    private Long showId;
    private List<String> seatNumbers;
}
