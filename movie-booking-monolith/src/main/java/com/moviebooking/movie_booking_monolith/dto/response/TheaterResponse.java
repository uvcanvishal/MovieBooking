package com.moviebooking.movie_booking_monolith.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer totalSeats;
}
