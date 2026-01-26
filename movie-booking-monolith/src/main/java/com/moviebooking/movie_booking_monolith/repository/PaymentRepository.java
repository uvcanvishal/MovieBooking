package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findByBookingId(Long bookingId);
}
