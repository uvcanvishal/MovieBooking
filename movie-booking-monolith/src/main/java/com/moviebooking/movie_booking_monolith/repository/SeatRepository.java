package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByTheaterId(Long theaterId);

    List<Seat> findByTheaterIdAndSeatNumberIn(Long theaterId, List<String> seatNumbers);

    List<Seat> findByTheaterIdAndStatus(Long theaterId, SeatStatus status);

    int countByTheaterIdAndStatus(Long theaterId, SeatStatus status);

    boolean existsByTheaterIdAndSeatNumber(Long theaterId, String seatNumber);

    List<Seat> findByStatusAndLockExpiryTimeBefore(SeatStatus status, LocalDateTime time);

    List<Seat> findByLockedByUserIdAndTheaterIdAndSeatNumberIn(Long userId, Long theaterId, List<String> seatNumbers);
}
