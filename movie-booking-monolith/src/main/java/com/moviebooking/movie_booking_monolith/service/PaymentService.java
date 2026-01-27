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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;



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

    public PaymentService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          SeatRepository seatRepository,
                          ExternalPaymentGatewayClient paymentGatewayClient) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatRepository = seatRepository;
        this.paymentGatewayClient = paymentGatewayClient;
    }

    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "initPaymentFallback")
    @Retry(name = "paymentInit")
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
        }

        bookingRepository.save(booking);
        paymentRepository.save(payment);
    }
}
