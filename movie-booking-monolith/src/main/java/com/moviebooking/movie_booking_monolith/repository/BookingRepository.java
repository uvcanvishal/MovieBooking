package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByShowId(Long showId);
}
