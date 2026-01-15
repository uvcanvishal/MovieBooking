package com.moviebooking.movie_booking_monolith.mapper;

import com.moviebooking.movie_booking_monolith.dto.request.TheaterRequest;
import com.moviebooking.movie_booking_monolith.dto.response.TheaterResponse;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TheaterMapper {

    public Theater toEntity(TheaterRequest request) {
        Theater theater = new Theater();
        theater.setName(request.getName());
        theater.setAddress(request.getAddress());
        theater.setCity(request.getCity());
        theater.setTotalSeats(request.getTotalSeats());
        return theater;
    }

    public void updateEntity(Theater theater, TheaterRequest request) {
        theater.setName(request.getName());
        theater.setAddress(request.getAddress());
        theater.setCity(request.getCity());
        theater.setTotalSeats(request.getTotalSeats());
    }

    public TheaterResponse toResponse(Theater theater) {
        return TheaterResponse.builder()
                .id(theater.getId())
                .name(theater.getName())
                .address(theater.getAddress())
                .city(theater.getCity())
                .totalSeats(theater.getTotalSeats())
                .build();
    }

    public List<TheaterResponse> toResponseList(List<Theater> theaters) {
        return theaters.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
