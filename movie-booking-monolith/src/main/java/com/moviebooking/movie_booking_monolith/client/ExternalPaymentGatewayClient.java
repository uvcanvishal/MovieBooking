package com.moviebooking.movie_booking_monolith.client;

import com.moviebooking.movie_booking_monolith.dto.response.PaymentInitResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalPaymentGatewayClient {

    /**
     * Day 19: Simulate Razorpay with network delay + 20% timeout
     * Triggers exponential retry backoff: 100ms → 200ms → 400ms → fallback
     */
    public PaymentInitResponse createOrder(Double amount, String currency) {
        // Simulate network delay (100-500ms)
        try {
            Thread.sleep((long) (Math.random() * 400 + 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestClientException("Interrupted");
        }

        // 20% timeout fail → Triggers RETRY backoff
        if (Math.random() < 0.2) {
            System.out.println("🛑 Gateway timeout (20%) → RETRY expected");
            throw new RestClientException("Gateway timeout");
        }

        // Success (80%)
        String orderId = "razorpay_order_" + UUID.randomUUID().toString().substring(0, 8);
        System.out.println("✅ Gateway success: " + orderId);
        return new PaymentInitResponse(null, amount, currency, orderId);
    }
}
