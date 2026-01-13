package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.*;
import com.moviebooking.movie_booking_monolith.entity.User;
import com.moviebooking.movie_booking_monolith.enums.Role;
import com.moviebooking.movie_booking_monolith.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhone(), user.getRole());
    }
}
