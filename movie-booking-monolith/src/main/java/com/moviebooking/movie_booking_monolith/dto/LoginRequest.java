package com.moviebooking.movie_booking_monolith.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
