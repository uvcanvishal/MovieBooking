package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.BookingRequest;
import com.moviebooking.movie_booking_monolith.dto.response.BookingResponse;
import com.moviebooking.movie_booking_monolith.entity.*;
import com.moviebooking.movie_booking_monolith.enums.BookingStatus;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.mapper.BookingMapper;
import com.moviebooking.movie_booking_monolith.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        Booking booking = findBookingById(id);
        return bookingMapper.toResponse(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getByUser(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookingMapper.toResponseList(bookings);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getByShow(Long showId) {
        List<Booking> bookings = bookingRepository.findByShowId(showId);
        return bookingMapper.toResponseList(bookings);
    }

    public BookingResponse create(BookingRequest request) {
        if (bookingRepository.existsActiveBookingForSeats(
                request.getUserId(),
                request.getShowId(),
                request.getSeatNumbers())) {
            throw new BadRequestException("You already have a confirmed booking for one or more of these seats");
        }
        // 1. Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // 2. Validate show
        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", request.getShowId()));

        // 3. Check show is in future
        if (show.getShowTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot book for a show that has already started");
        }

        // 4. Find requested seats
        List<Seat> seats = seatRepository.findByTheaterIdAndSeatNumberIn(
                show.getTheater().getId(), request.getSeatNumbers());

        if (seats.size() != request.getSeatNumbers().size()) {
            throw new BadRequestException("One or more seats not found in this theater");
        }

        // 5. Check seat availability
        for (Seat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " is not available");
            }
        }

        // 6. Calculate total amount
        double totalAmount = show.getPrice() * seats.size();

        // 7. Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setTotalAmount(totalAmount);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);

        // 8. Mark seats as booked
        seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
        seatRepository.saveAll(seats);

        return bookingMapper.toResponse(saved);
    }

    public BookingResponse cancel(Long id) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled");
        }

        // Check if show has started
        if (booking.getShow().getShowTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cannot cancel booking for a show that has already started");
        }

        // Release seats
        booking.getSeats().forEach(seat -> seat.setStatus(SeatStatus.AVAILABLE));
        seatRepository.saveAll(booking.getSeats());

        // Cancel booking
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelled = bookingRepository.save(booking);

        return bookingMapper.toResponse(cancelled);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }

    // NEW: Pagination methods
    @Transactional(readOnly = true)
    public Page<BookingResponse> getPage(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getPageByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUserId(userId, pageable)
                .map(bookingMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getPageByShow(Long showId, Pageable pageable) {
        return bookingRepository.findByShowId(showId, pageable)
                .map(bookingMapper::toResponse);
    }
}
