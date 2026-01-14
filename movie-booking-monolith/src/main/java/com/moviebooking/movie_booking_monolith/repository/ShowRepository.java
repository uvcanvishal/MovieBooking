package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieId(Long movieId);
    List<Show> findByTheaterId(Long theaterId);
}
