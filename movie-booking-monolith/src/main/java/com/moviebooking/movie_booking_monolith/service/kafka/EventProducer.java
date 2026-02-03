package com.moviebooking.movie_booking_monolith.service.kafka;

import com.moviebooking.movie_booking_monolith.config.KafkaTopics;
import com.moviebooking.movie_booking_monolith.dto.event.BookingEvent;
import com.moviebooking.movie_booking_monolith.dto.event.PaymentEvent;
import com.moviebooking.movie_booking_monolith.dto.event.SeatHeldEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<Void> publishBookingEvent(BookingEvent event) {
        return kafkaTemplate.send(KafkaTopics.BOOKING_EVENTS,
                        String.valueOf(event.getBookingId()), event)
                .handle((result, ex) -> {
                    if (ex == null) {
                        log.info("BookingEvent sent to {} partition={} offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send BookingEvent", ex);
                    }
                    return null;
                });
    }

    public CompletableFuture<Void> publishPaymentEvent(PaymentEvent event) {
        return kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS,
                        String.valueOf(event.getBookingId()), event)
                .handle((result, ex) -> {
                    if (ex == null) {
                        log.info("PaymentEvent sent to {} partition={} offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send PaymentEvent", ex);
                    }
                    return null;
                });
    }

    public CompletableFuture<Void> publishSeatHeldEvent(SeatHeldEvent event) {
        return kafkaTemplate.send("seat-held-events", event.getShowId(), event)
                .handle((result, ex) -> {
                    if (ex == null) {
                        log.info("SeatHeldEvent sent to {} partition={} offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send SeatHeldEvent", ex);
                    }
                    return null;
                });
    }


}
