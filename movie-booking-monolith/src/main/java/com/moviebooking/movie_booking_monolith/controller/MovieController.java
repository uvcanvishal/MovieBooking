package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.MovieRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.MovieResponse;
import com.moviebooking.movie_booking_monolith.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAll() {
        List<MovieResponse> movies = movieService.getAll();
        return ResponseEntity.ok(ApiResponse.success(movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getById(@PathVariable Long id) {
        MovieResponse movie = movieService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(movie));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieResponse>> create(@Valid @RequestBody MovieRequest request) {
        MovieResponse created = movieService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody MovieRequest request) {
        MovieResponse updated = movieService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Movie updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Movie deleted successfully", null));
    }
}
