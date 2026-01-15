package com.moviebooking.movie_booking_monolith.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "At least one seat must be selected")
    @Size(max = 10, message = "Cannot book more than 10 seats at once")
    private List<@NotBlank(message = "Seat number cannot be blank") String> seatNumbers;
}
