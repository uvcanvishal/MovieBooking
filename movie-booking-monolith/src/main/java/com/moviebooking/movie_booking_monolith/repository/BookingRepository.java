package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findByShowId(Long showId);

    Page<Booking> findByUserId(Long userId, Pageable pageable);
    Page<Booking> findByShowId(Long showId, Pageable pageable);
    Page<Booking> findAll(Pageable pageable);

    @Query("""
           SELECT COUNT(b) > 0
           FROM Booking b
           JOIN b.seats s
           WHERE b.user.id = :userId
             AND b.show.id = :showId
             AND s.seatNumber IN :seatNumbers
             AND b.status = com.moviebooking.movie_booking_monolith.enums.BookingStatus.CONFIRMED
           """)
    boolean existsActiveBookingForSeats(@Param("userId") Long userId,
                                        @Param("showId") Long showId,
                                        @Param("seatNumbers") List<String> seatNumbers);
}
