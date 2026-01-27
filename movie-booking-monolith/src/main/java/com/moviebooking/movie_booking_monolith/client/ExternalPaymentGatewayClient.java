package com.moviebooking.movie_booking_monolith.client;

import com.moviebooking.movie_booking_monolith.dto.response.PaymentInitResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Random;
import java.util.UUID;

@Component
public class ExternalPaymentGatewayClient {

    /**
     * Simulate Razorpay API: 70% success, 30% timeout/failure
     */
    public PaymentInitResponse createOrder(Double amount, String currency) {
        int rand = new Random().nextInt(10);
        if (rand < 3) {  // 30% failure
            System.out.println("❌ FAIL → FALLBACK expected");
            throw new RestClientException("Payment gateway timeout - Razorpay unavailable");
        }

        System.out.println("✅ SUCCESS → razorpay_order");
        // Success: return Razorpay‑like order
        return new PaymentInitResponse(
                null,
                amount,
                currency,
                "razorpay_order_" + UUID.randomUUID().toString().substring(0, 8)
        );
    }
}
