package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.repository.SeatRepository;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    public Seat createSeat(Long theaterId, String seatNumber) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with id " + theaterId));

        Seat seat = new Seat();
        seat.setTheater(theater);
        seat.setSeatNumber(seatNumber);
        seat.setStatus(SeatStatus.AVAILABLE);

        return seatRepository.save(seat);
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public List<Seat> getSeatsByTheater(Long theaterId) {
        return seatRepository.findByTheaterId(theaterId);
    }

    public List<Seat> getSeatsByTheaterAndNumbers(Long theaterId, List<String> seatNumbers) {
        return seatRepository.findByTheaterIdAndSeatNumberIn(theaterId, seatNumbers);
    }
}
