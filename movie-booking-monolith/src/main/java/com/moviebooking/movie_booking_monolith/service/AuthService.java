package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.LoginRequest;
import com.moviebooking.movie_booking_monolith.dto.request.RegisterRequest;
import com.moviebooking.movie_booking_monolith.dto.response.UserResponse;
import com.moviebooking.movie_booking_monolith.entity.User;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In production: hash with BCrypt

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // In production: use BCrypt matches
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
