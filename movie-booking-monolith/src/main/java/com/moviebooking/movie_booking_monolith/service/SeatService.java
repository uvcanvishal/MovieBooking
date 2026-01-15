package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.SeatRequest;
import com.moviebooking.movie_booking_monolith.dto.response.SeatResponse;
import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.mapper.SeatMapper;
import com.moviebooking.movie_booking_monolith.repository.SeatRepository;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private SeatMapper seatMapper;

    @Transactional(readOnly = true)
    public List<SeatResponse> getAll() {
        List<Seat> seats = seatRepository.findAll();
        return seatMapper.toResponseList(seats);
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getByTheater(Long theaterId) {
        List<Seat> seats = seatRepository.findByTheaterId(theaterId);
        return seatMapper.toResponseList(seats);
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getAvailableByTheater(Long theaterId) {
        List<Seat> seats = seatRepository.findByTheaterIdAndStatus(theaterId, SeatStatus.AVAILABLE);
        return seatMapper.toResponseList(seats);
    }

    public SeatResponse create(SeatRequest request) {
        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "id", request.getTheaterId()));

        // Check if seat already exists
        boolean exists = seatRepository.existsByTheaterIdAndSeatNumber(
                request.getTheaterId(), request.getSeatNumber());

        if (exists) {
            throw new BadRequestException("Seat " + request.getSeatNumber() +
                    " already exists in this theater");
        }

        Seat seat = new Seat();
        seat.setTheater(theater);
        seat.setSeatNumber(request.getSeatNumber());
        seat.setStatus(SeatStatus.AVAILABLE);

        Seat saved = seatRepository.save(seat);
        return seatMapper.toResponse(saved);
    }

    public List<SeatResponse> createBulk(Long theaterId, List<String> seatNumbers) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "id", theaterId));

        List<Seat> seats = seatNumbers.stream()
                .filter(sn -> !seatRepository.existsByTheaterIdAndSeatNumber(theaterId, sn))
                .map(sn -> {
                    Seat seat = new Seat();
                    seat.setTheater(theater);
                    seat.setSeatNumber(sn);
                    seat.setStatus(SeatStatus.AVAILABLE);
                    return seat;
                })
                .toList();

        List<Seat> saved = seatRepository.saveAll(seats);
        return seatMapper.toResponseList(saved);
    }
}
