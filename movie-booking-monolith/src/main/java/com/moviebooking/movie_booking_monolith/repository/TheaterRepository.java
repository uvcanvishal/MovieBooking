package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> {
}