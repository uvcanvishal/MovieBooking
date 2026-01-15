package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SeatRequest {

    @NotNull(message = "Theater ID is required")
    private Long theaterId;

    @NotBlank(message = "Seat number is required")
    @Pattern(regexp = "^[A-Z][0-9]{1,3}$", message = "Seat number must be in format like A1, B12, C123")
    private String seatNumber;
}
