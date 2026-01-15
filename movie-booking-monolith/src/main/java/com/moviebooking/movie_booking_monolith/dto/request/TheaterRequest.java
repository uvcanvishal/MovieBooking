package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TheaterRequest {

    @NotBlank(message = "Theater name is required")
    @Size(min = 1, max = 200, message = "Theater name must be between 1 and 200 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City name cannot exceed 100 characters")
    private String city;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Theater must have at least 1 seat")
    @Max(value = 1000, message = "Theater cannot have more than 1000 seats")
    private Integer totalSeats;
}
