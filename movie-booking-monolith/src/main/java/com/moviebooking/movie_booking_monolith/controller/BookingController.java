package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.BookingRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.BookingResponse;
import com.moviebooking.movie_booking_monolith.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getByUser(@PathVariable Long userId) {
        List<BookingResponse> bookings = bookingService.getByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/by-show/{showId}")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getByShow(@PathVariable Long showId) {
        List<BookingResponse> bookings = bookingService.getByShow(showId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking confirmed successfully", created));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(@PathVariable Long id) {
        BookingResponse cancelled = bookingService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", cancelled));
    }
}
