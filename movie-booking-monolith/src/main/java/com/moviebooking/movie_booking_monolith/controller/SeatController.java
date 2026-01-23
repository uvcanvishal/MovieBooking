package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.SeatRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.SeatResponse;
import com.moviebooking.movie_booking_monolith.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAll() {
        List<SeatResponse> seats = seatService.getAll();
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @GetMapping("/by-theater/{theaterId}")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getByTheater(@PathVariable Long theaterId) {
        List<SeatResponse> seats = seatService.getByTheater(theaterId);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @GetMapping("/available/{theaterId}")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> getAvailableByTheater(@PathVariable Long theaterId) {
        List<SeatResponse> seats = seatService.getAvailableByTheater(theaterId);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeatResponse>> create(@Valid @RequestBody(required = false) SeatRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Seat request body required"));
        }
        SeatResponse created = seatService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seat created successfully", created));
    }

    @PostMapping("/bulk/{theaterId}")
    public ResponseEntity<ApiResponse<List<SeatResponse>>> createBulk(@PathVariable Long theaterId,
                                                                      @RequestBody List<String> seatNumbers) {
        List<SeatResponse> created = seatService.createBulk(theaterId, seatNumbers);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Seats created successfully", created));
    }
}
