package com.moviebooking.movie_booking_monolith.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowResponse {

    private Long id;
    private Long movieId;
    private String movieName;
    private Long theaterId;
    private String theaterName;
    private String theaterCity;
    private LocalDateTime showTime;
    private Double price;
    private Integer availableSeats;
}
