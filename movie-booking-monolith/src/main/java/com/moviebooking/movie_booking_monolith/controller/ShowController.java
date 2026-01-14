package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.entity.Show;
import com.moviebooking.movie_booking_monolith.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    // Create show (same behavior as before, but via service)
    @PostMapping
    public ResponseEntity<Show> createShow(@RequestParam Long movieId,
                                           @RequestParam Long theaterId,
                                           @RequestParam Double price) {
        // For now, showTime = tomorrow; later you can pass as param or JSON.
        LocalDateTime showTime = LocalDateTime.now().plusDays(1);
        Show show = showService.createShow(movieId, theaterId, price, showTime);
        return ResponseEntity.ok(show);
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(showService.getShowById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-movie/{movieId}")
    public List<Show> getShowsByMovie(@PathVariable Long movieId) {
        return showService.getShowsByMovie(movieId);
    }

    @GetMapping("/by-theater/{theaterId}")
    public List<Show> getShowsByTheater(@PathVariable Long theaterId) {
        return showService.getShowsByTheater(theaterId);
    }
}
