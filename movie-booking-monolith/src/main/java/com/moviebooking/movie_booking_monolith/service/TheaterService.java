package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.TheaterRequest;
import com.moviebooking.movie_booking_monolith.dto.response.TheaterResponse;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.mapper.TheaterMapper;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TheaterService {

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private TheaterMapper theaterMapper;

    @Transactional(readOnly = true)
    public List<TheaterResponse> getAll() {
        List<Theater> theaters = theaterRepository.findAll();
        return theaterMapper.toResponseList(theaters);
    }

    @Transactional(readOnly = true)
    public TheaterResponse getById(Long id) {
        Theater theater = findTheaterById(id);
        return theaterMapper.toResponse(theater);
    }

    public TheaterResponse create(TheaterRequest request) {
        Theater theater = theaterMapper.toEntity(request);
        Theater saved = theaterRepository.save(theater);
        return theaterMapper.toResponse(saved);
    }

    public TheaterResponse update(Long id, TheaterRequest request) {
        Theater theater = findTheaterById(id);
        theaterMapper.updateEntity(theater, request);
        Theater updated = theaterRepository.save(theater);
        return theaterMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Theater theater = findTheaterById(id);
        theaterRepository.delete(theater);
    }

    private Theater findTheaterById(Long id) {
        return theaterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "id", id));
    }
}
