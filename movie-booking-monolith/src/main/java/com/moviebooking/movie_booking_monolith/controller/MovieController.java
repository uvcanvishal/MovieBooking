package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movieDetails) {
        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setName(movieDetails.getName());
                    movie.setDuration(movieDetails.getDuration());
                    movie.setRating(movieDetails.getRating());
                    movie.setGenre(movieDetails.getGenre());
                    movie.setLanguage(movieDetails.getLanguage());
                    movie.setReleaseDate(movieDetails.getReleaseDate());
                    return ResponseEntity.ok(movieRepository.save(movie));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
