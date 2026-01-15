package com.moviebooking.movie_booking_monolith.dto.request;

import com.moviebooking.movie_booking_monolith.enums.Genre;
import com.moviebooking.movie_booking_monolith.enums.Language;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovieRequest {

    @NotBlank(message = "Movie name is required")
    @Size(min = 1, max = 200, message = "Movie name must be between 1 and 200 characters")
    private String name;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 600, message = "Duration cannot exceed 600 minutes")
    private Integer duration;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "10.0", message = "Rating cannot exceed 10")
    private Double rating;

    @NotNull(message = "Genre is required")
    private Genre genre;

    @NotNull(message = "Language is required")
    private Language language;

    @NotNull(message = "Release date is required")
    @PastOrPresent(message = "Release date cannot be in the future")
    private LocalDate releaseDate;
}
