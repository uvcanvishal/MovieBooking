package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Theater getTheaterById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Theater not found with id: " + id));
    }

    public Theater createTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    public Theater updateTheater(Long id, Theater theaterDetails) {
        Theater theater = getTheaterById(id);
        theater.setName(theaterDetails.getName());
        theater.setAddress(theaterDetails.getAddress());
        theater.setCity(theaterDetails.getCity());
        theater.setTotalSeats(theaterDetails.getTotalSeats());
        return theaterRepository.save(theater);
    }

    public void deleteTheater(Long id) {
        Theater theater = getTheaterById(id);
        theaterRepository.delete(theater);
    }
}
