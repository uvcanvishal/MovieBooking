package com.moviebooking.movie_booking_monolith.dto;

import com.moviebooking.movie_booking_monolith.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
}
