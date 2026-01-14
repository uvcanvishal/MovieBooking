package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @PostMapping
    public ResponseEntity<Seat> createSeat(@RequestParam Long theaterId,
                                           @RequestParam String seatNumber) {
        Seat seat = seatService.createSeat(theaterId, seatNumber);
        return ResponseEntity.ok(seat);
    }

    @GetMapping
    public List<Seat> getAllSeats() {
        return seatService.getAllSeats();
    }

    @GetMapping("/by-theater/{theaterId}")
    public List<Seat> getSeatsByTheater(@PathVariable Long theaterId) {
        return seatService.getSeatsByTheater(theaterId);
    }
}
