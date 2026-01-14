package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.BookingResponse;
import com.moviebooking.movie_booking_monolith.dto.CreateBookingRequest;
import com.moviebooking.movie_booking_monolith.entity.Booking;
import com.moviebooking.movie_booking_monolith.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.getBookingById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-user/{userId}")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/by-show/{showId}")
    public List<Booking> getBookingsByShow(@PathVariable Long showId) {
        return bookingService.getBookingsByShow(showId);
    }
}
