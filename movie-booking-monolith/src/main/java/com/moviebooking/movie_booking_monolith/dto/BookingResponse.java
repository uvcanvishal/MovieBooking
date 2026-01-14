package com.moviebooking.movie_booking_monolith.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String userName;
    private String movieName;
    private String theaterName;
    private int seatsCount;
    private Double totalAmount;
    private String status;
}
