package com.moviebooking.movie_booking_monolith.mapper;

import com.moviebooking.movie_booking_monolith.dto.response.SeatResponse;
import com.moviebooking.movie_booking_monolith.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatMapper {

    public SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .theaterId(seat.getTheater().getId())
                .theaterName(seat.getTheater().getName())
                .status(seat.getStatus())
                .build();
    }

    public List<SeatResponse> toResponseList(List<Seat> seats) {
        return seats.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
