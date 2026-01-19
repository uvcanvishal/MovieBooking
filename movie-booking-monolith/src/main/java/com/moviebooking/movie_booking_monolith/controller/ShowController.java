package com.moviebooking.movie_booking_monolith.controller;

import com.moviebooking.movie_booking_monolith.dto.request.ShowRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ApiResponse;
import com.moviebooking.movie_booking_monolith.dto.response.ShowResponse;
import com.moviebooking.movie_booking_monolith.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    @Autowired
    private ShowService showService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getAll() {
        List<ShowResponse> shows = showService.getAll();
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowResponse>> getById(@PathVariable Long id) {
        ShowResponse show = showService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(show));
    }

    @GetMapping("/by-movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getByMovie(@PathVariable Long movieId) {
        List<ShowResponse> shows = showService.getByMovie(movieId);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @GetMapping("/by-theater/{theaterId}")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getByTheater(@PathVariable Long theaterId) {
        List<ShowResponse> shows = showService.getByTheater(theaterId);
        return ResponseEntity.ok(ApiResponse.success(shows));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShowResponse>> create(@Valid @RequestBody ShowRequest request) {
        ShowResponse created = showService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Show created successfully", created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        showService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Show deleted successfully", null));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ShowResponse>>> getPage(
            @PageableDefault(size = 10, sort = "showTime") Pageable pageable) {
        Page<ShowResponse> page = showService.getPage(pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/page/by-movie/{movieId}")
    public ResponseEntity<ApiResponse<Page<ShowResponse>>> getPageByMovie(
            @PathVariable Long movieId,
            @PageableDefault(size = 10, sort = "showTime") Pageable pageable) {
        Page<ShowResponse> page = showService.getPageByMovie(movieId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/page/by-theater/{theaterId}")
    public ResponseEntity<ApiResponse<Page<ShowResponse>>> getPageByTheater(
            @PathVariable Long theaterId,
            @PageableDefault(size = 10, sort = "showTime") Pageable pageable) {
        Page<ShowResponse> page = showService.getPageByTheater(theaterId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
