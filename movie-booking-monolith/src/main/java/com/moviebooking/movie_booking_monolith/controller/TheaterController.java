package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.service.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theaterService;

    @GetMapping
    public List<Theater> getAllTheaters() {
        return theaterService.getAllTheaters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheaterById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(theaterService.getTheaterById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Theater> createTheater(@RequestBody Theater theater) {
        return ResponseEntity.ok(theaterService.createTheater(theater));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long id, @RequestBody Theater theaterDetails) {
        try {
            return ResponseEntity.ok(theaterService.updateTheater(id, theaterDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id) {
        try {
            theaterService.deleteTheater(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
