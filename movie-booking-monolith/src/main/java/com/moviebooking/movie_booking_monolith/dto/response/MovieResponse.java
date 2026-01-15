package com.moviebooking.movie_booking_monolith.dto.response;

import com.moviebooking.movie_booking_monolith.enums.Genre;
import com.moviebooking.movie_booking_monolith.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String name;
    private BigDecimal duration;
    private Double rating;
    private Genre genre;
    private Language language;
    private LocalDate releaseDate;
}
