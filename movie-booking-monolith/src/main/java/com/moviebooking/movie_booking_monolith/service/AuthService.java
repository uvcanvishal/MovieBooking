package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.LoginRequest;
import com.moviebooking.movie_booking_monolith.dto.request.RegisterRequest;
import com.moviebooking.movie_booking_monolith.dto.response.LoginResponse;
import com.moviebooking.movie_booking_monolith.dto.response.UserResponse;
import com.moviebooking.movie_booking_monolith.entity.User;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.repository.UserRepository;
import com.moviebooking.movie_booking_monolith.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;  // NEW

    @Autowired
    private JwtService jwtService;  // NEW

    @Autowired
    private PasswordEncoder passwordEncoder;  // NEW (for register hashing)

    public UserResponse register(RegisterRequest request) {
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));  // NOW HASHED

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .build();
    }

    public LoginResponse login(LoginRequest request) {  // NOW RETURNS LoginResponse
        // Use Spring Security to authenticate (handles BCrypt check)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Get UserDetails after successful auth
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate JWT
        String token = jwtService.generateToken(userDetails);

        // Return your existing UserResponse + token
        return LoginResponse.builder()
                .token(token)
                .expiresInMs(86400000L)
                .user(UserResponse.builder()
                        .id(getUserByEmail(request.getEmail()).getId())
                        .name(getUserByEmail(request.getEmail()).getName())
                        .email(request.getEmail())
                        .build())
                .build();
    }

    // Helper (reuse your User entity)
    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
