package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterRepository theaterRepository;

    @GetMapping
    public List<Theater> getAllTheaters(){
        return theaterRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Theater> getTheaterById(@PathVariable Long id){
        return theaterRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Theater createTheater(@RequestBody Theater theater){
        return theaterRepository.save(theater);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Theater> updateTheater(@PathVariable Long id,@RequestBody Theater theaterDetails){
        return theaterRepository.findById(id)
                .map(theater -> {
                    theater.setName(theaterDetails.getName());
                    theater.setAddress(theaterDetails.getAddress());
                    theater.setCity(theaterDetails.getCity());
                    theater.setTotalSeats(theaterDetails.getTotalSeats());
                    return ResponseEntity.ok(theaterRepository.save(theater));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheater(@PathVariable Long id){
        if(theaterRepository.existsById(id)){
            theaterRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
