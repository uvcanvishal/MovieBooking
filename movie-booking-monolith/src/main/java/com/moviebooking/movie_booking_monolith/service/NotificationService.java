package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.event.BookingEvent;
import com.moviebooking.movie_booking_monolith.dto.event.PaymentEvent;
import com.moviebooking.movie_booking_monolith.dto.event.SeatHeldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    public void sendBookingConfirmation(BookingEvent event) {
        log.info("📧 EMAIL SENT: Booking {} {} for user {} - Show {} Seats {}",
                event.getEventType(),
                event.getBookingId(),
                event.getUserId(),
                event.getShowId(),
                event.getSeatIds());

        // Real impl: EmailService.send(bookingEmailTemplate(event))
    }

    public void sendBookingFailed(BookingEvent event) {
        log.info("📧 EMAIL SENT: Booking {} FAILED for user {} - {}",
                event.getBookingId(),
                event.getUserId(),
                event.getSeatIds());
    }

    public void sendPaymentInitiated(PaymentEvent event) {
        log.info("📧 SMS SENT: Pay for booking {} (order {}) Amount ₹{:.0f}",
                event.getBookingId(),
                event.getGatewayOrderId(),
                String.format("%.0f", event.getAmount()));
    }

    public void sendPaymentSuccess(PaymentEvent event) {
        log.info("🎉 CONFIRMATION EMAIL: Payment SUCCESS for booking {} Payment {}",
                event.getBookingId(),
                event.getGatewayPaymentId());
    }

    public void sendPaymentFailed(PaymentEvent event) {
        log.info("⚠️  EMAIL: Payment FAILED for booking {}: {}",
                event.getBookingId(),
                event.getFailureReason());
    }

    @KafkaListener(topics = "seat-held-events", groupId = "movie-notifications", containerFactory = "seatHeldListenerFactory")
    public void handleSeatHeld(SeatHeldEvent event) {
        log.info("📥 Seat held: showId={} seatId={} user={} holdUntil={}",
                event.getShowId(),
                event.getSeatId(),
                event.getUserId(),
                event.getHoldUntil());
        log.info("📧 EMAIL: Seat lock notification sent");
    }



}
