package com.moviebooking.movie_booking_monolith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailService {
    @Async
    public CompletableFuture<Void> sendBookingConfirmationAsync(Long bookingId, String userEmail) {
        log.info("🚀 ASYNC: Sending confirmation for booking {} → {}", bookingId, userEmail);
        try {
            Thread.sleep(4000);  // Simulate real email (SendGrid/AWS SES)
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("✅ ASYNC: Email sent for booking {}", bookingId);
        return CompletableFuture.completedFuture(null);
    }
}
