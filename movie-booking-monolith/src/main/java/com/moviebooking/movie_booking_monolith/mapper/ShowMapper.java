package com.moviebooking.movie_booking_monolith.mapper;

import com.moviebooking.movie_booking_monolith.dto.response.ShowResponse;
import com.moviebooking.movie_booking_monolith.entity.Show;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShowMapper {

    @Autowired
    private SeatRepository seatRepository;

    public ShowResponse toResponse(Show show) {
        int availableSeats = seatRepository.countByTheaterIdAndStatus(
                show.getTheater().getId(), SeatStatus.AVAILABLE);

        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieName(show.getMovie().getName())
                .theaterId(show.getTheater().getId())
                .theaterName(show.getTheater().getName())
                .theaterCity(show.getTheater().getCity())
                .showTime(show.getShowTime())
                .price(show.getPrice())
                .availableSeats(availableSeats)
                .build();
    }

    public List<ShowResponse> toResponseList(List<Show> shows) {
        return shows.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
