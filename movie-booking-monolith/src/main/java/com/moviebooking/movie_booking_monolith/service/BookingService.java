package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.BookingResponse;
import com.moviebooking.movie_booking_monolith.dto.CreateBookingRequest;
import com.moviebooking.movie_booking_monolith.entity.*;
import com.moviebooking.movie_booking_monolith.enums.BookingStatus;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    public BookingResponse createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<Seat> seats = seatRepository.findByTheaterIdAndSeatNumberIn(
                show.getTheater().getId(), request.getSeatNumbers());

        if (seats.size() != request.getSeatNumbers().size()) {
            throw new RuntimeException("Some seats not found for this theater");
        }

        for (Seat seat : seats) {
            if (!seat.getStatus().equals(SeatStatus.AVAILABLE)) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " not available");
            }
        }

        double totalAmount = show.getPrice() * seats.size();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setTotalAmount(totalAmount);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }

        return new BookingResponse(
                saved.getId(),
                user.getName(),
                show.getMovie().getName(),
                show.getTheater().getName(),
                seats.size(),
                totalAmount,
                saved.getStatus().name()
        );
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id " + id));
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByShow(Long showId) {
        return bookingRepository.findByShowId(showId);
    }
}
