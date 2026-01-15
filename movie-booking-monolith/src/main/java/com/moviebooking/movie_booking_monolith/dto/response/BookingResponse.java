package com.moviebooking.movie_booking_monolith.dto.response;

import com.moviebooking.movie_booking_monolith.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private Long userId;
    private String userName;
    private Long showId;
    private String movieName;
    private String theaterName;
    private LocalDateTime showTime;
    private List<String> seatNumbers;
    private Double totalAmount;
    private LocalDateTime bookingTime;
    private BookingStatus status;
}
