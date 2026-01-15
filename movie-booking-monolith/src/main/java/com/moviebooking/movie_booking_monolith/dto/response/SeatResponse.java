package com.moviebooking.movie_booking_monolith.dto.response;

import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponse {

    private Long id;
    private String seatNumber;
    private Long theaterId;
    private String theaterName;
    private SeatStatus status;
}
