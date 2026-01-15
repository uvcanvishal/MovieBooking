package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Theater ID is required")
    private Long theaterId;

    @NotNull(message = "Show time is required")
    @Future(message = "Show time must be in the future")
    private LocalDateTime showTime;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @DecimalMax(value = "10000.0", message = "Price cannot exceed 10000")
    private Double price;
}
