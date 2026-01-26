package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.PaymentCallbackRequest;
import com.moviebooking.movie_booking_monolith.dto.request.PaymentInitRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.PaymentInitResponse;
import com.moviebooking.movie_booking_monolith.service.AuthService;
import com.moviebooking.movie_booking_monolith.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    @PostMapping("/init")
    @Operation(summary = "Initiate payment for a booking (Day 8)")
    public ResponseEntity<ApiResponse<PaymentInitResponse>> initiate(
            @Valid @RequestBody PaymentInitRequest request,
            Authentication authentication) {

        Long userId = authService.getUserIdFromAuthentication(authentication);
        PaymentInitResponse response = paymentService.initiatePayment(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Payment initiated", response));
    }

    @PostMapping("/callback")
    @Operation(summary = "Handle payment callback (simulate Razorpay webhook)")
    public ResponseEntity<ApiResponse<Void>> callback(
            @Valid @RequestBody PaymentCallbackRequest request) {

        paymentService.handleCallback(request);
        return ResponseEntity.ok(ApiResponse.success("Payment processed", null));
    }
}
