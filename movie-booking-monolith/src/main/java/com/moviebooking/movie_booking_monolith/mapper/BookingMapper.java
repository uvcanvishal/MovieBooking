package com.moviebooking.movie_booking_monolith.mapper;

import com.moviebooking.movie_booking_monolith.dto.response.BookingResponse;
import com.moviebooking.movie_booking_monolith.entity.Booking;
import com.moviebooking.movie_booking_monolith.entity.Seat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        List<String> seatNumbers = booking.getSeats().stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .showId(booking.getShow().getId())
                .movieName(booking.getShow().getMovie().getName())
                .theaterName(booking.getShow().getTheater().getName())
                .showTime(booking.getShow().getShowTime())
                .seatNumbers(seatNumbers)
                .totalAmount(booking.getTotalAmount())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .build();
    }

    public List<BookingResponse> toResponseList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
