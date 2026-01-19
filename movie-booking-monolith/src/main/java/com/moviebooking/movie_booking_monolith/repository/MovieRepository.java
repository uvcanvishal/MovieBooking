package com.moviebooking.movie_booking_monolith.repository;

import com.moviebooking.movie_booking_monolith.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Movie> findByGenreIgnoreCase(String genre, Pageable pageable);
}
