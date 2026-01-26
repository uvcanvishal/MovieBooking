package com.moviebooking.movie_booking_monolith.service;

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

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SeatRepository seatRepository;

    public PaymentService(BookingRepository bookingRepository,
                          PaymentRepository paymentRepository,
                          SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.seatRepository = seatRepository;
    }

    public PaymentInitResponse initiatePayment(Long userId, PaymentInitRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        // Ensure booking belongs to user and is in PENDING state
        if (!booking.getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot pay for another user's booking");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Booking is not in PENDING state");
        }

        // For now we use INR and dummy gatewayOrderId
        String currency = "INR";
        String gatewayOrderId = "order_" + UUID.randomUUID();

        Payment payment = paymentRepository.findByBookingId(booking.getId());
        if (payment == null) {
            payment = Payment.builder()
                    .booking(booking)
                    .amount(booking.getTotalAmount())
                    .currency(currency)
                    .gatewayOrderId(gatewayOrderId)
                    .status(PaymentStatus.INITIATED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            // reuse existing row for retries
            payment.setGatewayOrderId(gatewayOrderId);
            payment.setStatus(PaymentStatus.INITIATED);
            payment.setUpdatedAt(LocalDateTime.now());
        }

        paymentRepository.save(payment);

        return new PaymentInitResponse(
                booking.getId(),
                booking.getTotalAmount(),
                currency,
                gatewayOrderId
        );
    }

    public void handleCallback(PaymentCallbackRequest request) {
        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.bookingId()));

        Payment payment = paymentRepository.findByBookingId(booking.getId());
        if (payment == null) {
            throw new BadRequestException("Payment not found for booking");
        }

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
