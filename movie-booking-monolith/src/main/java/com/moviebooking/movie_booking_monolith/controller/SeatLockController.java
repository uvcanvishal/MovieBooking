package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.SeatLockRequest;
import com.moviebooking.movie_booking_monolith.dto.response.SeatLockResponse;
import com.moviebooking.movie_booking_monolith.service.AuthService;
import com.moviebooking.movie_booking_monolith.service.SeatLockService;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seat-locks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")  // Swagger JWT
public class SeatLockController {

    private final SeatLockService seatLockService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Lock seats for payment (Day 7)")
    public ResponseEntity<ApiResponse<SeatLockResponse>> lockSeats(
            @Valid @RequestBody SeatLockRequest request,
            Authentication authentication) {

        Long userId = authService.getUserIdFromAuthentication(authentication);  // Your Day 6 method
        SeatLockResponse response = seatLockService.lockSeats(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Seats locked successfully", response));
    }
}
