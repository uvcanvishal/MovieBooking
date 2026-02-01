package com.moviebooking.movie_booking_monolith.consumer;

import com.moviebooking.movie_booking_monolith.config.KafkaTopics;
import com.moviebooking.movie_booking_monolith.dto.event.BookingEvent;
import com.moviebooking.movie_booking_monolith.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.BOOKING_EVENTS,
            groupId = "movie-notifications",
            containerFactory = "bookingListenerFactory")
    public void handleBookingEvent(BookingEvent event) {
        log.info("📥 Received BookingEvent: {} - {}", event.getEventType(), event.getBookingId());

        switch (event.getEventType()) {
            case "BOOKING_CREATED" -> notificationService.sendBookingConfirmation(event);
            case "BOOKING_CONFIRMED" -> notificationService.sendBookingConfirmation(event);
            case "BOOKING_FAILED" -> notificationService.sendBookingFailed(event);
            default -> log.warn("Unknown BookingEvent type: {}", event.getEventType());
        }
    }
}
