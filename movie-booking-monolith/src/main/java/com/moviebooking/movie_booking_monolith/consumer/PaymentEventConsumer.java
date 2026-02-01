package com.moviebooking.movie_booking_monolith.consumer;

import com.moviebooking.movie_booking_monolith.config.KafkaTopics;
import com.moviebooking.movie_booking_monolith.dto.event.PaymentEvent;
import com.moviebooking.movie_booking_monolith.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = "movie-notifications",
            containerFactory = "paymentListenerFactory")
    public void handlePaymentEvent(PaymentEvent event) {
        log.info("💳 Received PaymentEvent: {} - Booking {}",
                event.getStatus(), event.getBookingId());

        switch (event.getStatus()) {
            case "INITIATED" -> notificationService.sendPaymentInitiated(event);
            case "SUCCESS" -> notificationService.sendPaymentSuccess(event);
            case "FAILED" -> notificationService.sendPaymentFailed(event);
            default -> log.warn("Unknown PaymentEvent status: {}", event.getStatus());
        }
    }
}
