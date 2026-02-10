package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.client.ExternalPaymentGatewayClient;
import com.moviebooking.movie_booking_monolith.dto.request.PaymentCallbackRequest;
import com.moviebooking.movie_booking_monolith.dto.request.PaymentInitRequest;
import com.moviebooking.movie_booking_monolith.dto.response.PaymentInitResponse;
import com.moviebooking.movie_booking_monolith.entity.Booking;
import com.moviebooking.movie_booking_monolith.entity.Payment;
import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.enums.BookingStatus;
import com.moviebooking.movie_booking_monolith.enums.PaymentStatus;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.repository.BookingRepository;
import com.moviebooking.movie_booking_monolith.repository.PaymentRepository;
import com.moviebooking.movie_booking_monolith.repository.SeatRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import com.moviebooking.movie_booking_monolith.service.kafka.EventProducer;
import com.moviebooking.movie_booking_monolith.dto.event.PaymentEvent;
import com.moviebooking.movie_booking_monolith.dto.event.BookingEvent;
import java.time.Instant;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SeatRepository seatRepository;
    private final ExternalPaymentGatewayClient paymentGatewayClient;
    private final EventProducer eventProducer;

    public PaymentService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          SeatRepository seatRepository,
                          ExternalPaymentGatewayClient paymentGatewayClient,
                          EventProducer eventProducer) {  // ← ADD THIS
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatRepository = seatRepository;
        this.paymentGatewayClient = paymentGatewayClient;
        this.eventProducer = eventProducer;  // ← ADD THIS
    }


    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "initPaymentFallback")
    @Retry(name = "paymentInit")
    @RateLimiter(name = "paymentInit", fallbackMethod = "rateLimitFallback")
    public PaymentInitResponse initiatePayment(Long userId, PaymentInitRequest request) {
        // 1. Your existing validation
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("Cannot pay for another user's booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking not PENDING");
        }

        // 2. Create/update Payment row (your existing logic)
        List<Payment> payments = paymentRepository.findByBookingId(booking.getId());
        Payment payment;
        if (payments.isEmpty()) {
            // First payment attempt
            payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .currency("INR")
                    .status(PaymentStatus.INITIATED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            // Update latest payment (retry)
            payment = payments.get(payments.size() - 1);
            payment.setStatus(PaymentStatus.INITIATED);
            payment.setUpdatedAt(LocalDateTime.now());
        }

        // 3. CALL FLAKY EXTERNAL GATEWAY ← This gets CircuitBreaker/Retry
        PaymentInitResponse gatewayResponse =
                paymentGatewayClient.createOrder(booking.getTotalAmount(), "INR");

        // 4. Save gateway orderId
        payment.setGatewayOrderId(gatewayResponse.gatewayOrderId());
        paymentRepository.save(payment);

        // ← ADD THIS (after paymentRepository.save(payment))
        PaymentEvent initEvent = PaymentEvent.builder()
                .bookingId(booking.getId())
                .userId(userId)
                .gatewayOrderId(gatewayResponse.gatewayOrderId())
                .status("INITIATED")
                .amount(booking.getTotalAmount())
                .eventTime(Instant.now())
                .build();
        eventProducer.publishPaymentEvent(initEvent);
        // ← END


        return new PaymentInitResponse(
                booking.getId(),
                booking.getTotalAmount(),
                "INR",
                gatewayResponse.gatewayOrderId()
        );
    }

    /**
     * Fallback: When CircuitBreaker OPEN or all retries fail
     */
    public PaymentInitResponse initPaymentFallback(Long userId, PaymentInitRequest request, Exception ex) {
        System.err.println("PAYMENT GATEWAY DOWN: " + ex.getMessage() + " → FALLBACK MODE");

        // Create payment record without gateway (user retries later)
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .currency("INR")
                .gatewayOrderId("FALLBACK_" + System.currentTimeMillis())
                .status(PaymentStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        return new PaymentInitResponse(
                request.bookingId(),
                booking.getTotalAmount(),
                "INR",
                "FALLBACK_PAYMENT_RETRY_LATER"
        );
    }


    public void handleCallback(PaymentCallbackRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        List<Payment> payments = paymentRepository.findByBookingId(booking.getId());
        if (payments.isEmpty()) {
            throw new BadRequestException("Payment not found for booking");
        }
        Payment payment = payments.get(payments.size() - 1);  // Latest payment

        // Typically you verify signature + order id here (Razorpay HMAC etc.)
        // For Day 8, trust the "success" flag.
        payment.setGatewayPaymentId(request.gatewayPaymentId());
        payment.setGatewayOrderId(request.gatewayOrderId());
        payment.setUpdatedAt(LocalDateTime.now());

        if (request.success()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            // Confirm booking, mark seats as BOOKED
            booking.setStatus(BookingStatus.CONFIRMED);

            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.BOOKED);
                seat.setLockExpiryTime(null);
                seat.setLockedByUserId(null);
            }
            seatRepository.saveAll(booking.getSeats());
            // ← ADD THIS (in if(request.success()))
            PaymentEvent successEvent = PaymentEvent.builder()
                    .bookingId(booking.getId())
                    .userId(booking.getUser().getId())
                    .gatewayOrderId(request.gatewayOrderId())
                    .gatewayPaymentId(request.gatewayPaymentId())
                    .status("SUCCESS")
                    .amount(booking.getTotalAmount())
                    .eventTime(Instant.now())
                    .build();
            eventProducer.publishPaymentEvent(successEvent);

            BookingEvent confirmedEvent = BookingEvent.builder()
                    .bookingId(booking.getId())
                    .userId(booking.getUser().getId())
                    .showId(booking.getShow().getId())
                    .seatIds(booking.getSeats().stream().map(s -> s.getId()).collect(Collectors.toList()))
                    .status("CONFIRMED")
                    .amount(booking.getTotalAmount())
                    .eventTime(Instant.now())
                    .eventType("BOOKING_CONFIRMED")
                    .build();
            eventProducer.publishBookingEvent(confirmedEvent);
            // ← END (before bookingRepository.save)

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            booking.setStatus(BookingStatus.FAILED);
            // Optionally release lock immediately for failed payments
            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setLockExpiryTime(null);
                seat.setLockedByUserId(null);
            }
            seatRepository.saveAll(booking.getSeats());

            // ← ADD THIS (in else)
            PaymentEvent failedEvent = PaymentEvent.builder()
                    .bookingId(booking.getId())
                    .userId(booking.getUser().getId())
                    .gatewayOrderId(request.gatewayOrderId())
                    .gatewayPaymentId(request.gatewayPaymentId())
                    .status("FAILED")
                    .amount(booking.getTotalAmount())
                    .eventTime(Instant.now())
                    .failureReason("PAYMENT_FAILED")
                    .build();
            eventProducer.publishPaymentEvent(failedEvent);

            BookingEvent failedBookingEvent = BookingEvent.builder()
                    .bookingId(booking.getId())
                    .userId(booking.getUser().getId())
                    .showId(booking.getShow().getId())
                    .seatIds(booking.getSeats().stream().map(s -> s.getId()).collect(Collectors.toList()))
                    .status("FAILED")
                    .amount(booking.getTotalAmount())
                    .eventTime(Instant.now())
                    .eventType("BOOKING_FAILED")
                    .build();
            eventProducer.publishBookingEvent(failedBookingEvent);
            // ← END (before bookingRepository.save)

        }

        bookingRepository.save(booking);
        paymentRepository.save(payment);
    }

    public PaymentInitResponse rateLimitFallback(Long userId, PaymentInitRequest request, Throwable t) {
        System.err.println("🚫 RATE LIMITED: User " + userId + " exceeded 100 req/min limit");

        return new PaymentInitResponse(
                request.bookingId(),
                0.0,
                "INR",
                "RATE_LIMITED_TRY_AGAIN_IN_1_MINUTE"
        );
    }
}
