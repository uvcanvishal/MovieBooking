package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.TheaterRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.TheaterResponse;
import com.moviebooking.movie_booking_monolith.service.TheaterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    @Autowired
    private TheaterService theaterService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheaterResponse>>> getAll() {
        List<TheaterResponse> theaters = theaterService.getAll();
        return ResponseEntity.ok(ApiResponse.success(theaters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> getById(@PathVariable Long id) {
        TheaterResponse theater = theaterService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(theater));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TheaterResponse>> create(@Valid @RequestBody TheaterRequest request) {
        TheaterResponse created = theaterService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Theater created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TheaterResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody TheaterRequest request) {
        TheaterResponse updated = theaterService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Theater updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        theaterService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Theater deleted successfully", null));
    }
}
