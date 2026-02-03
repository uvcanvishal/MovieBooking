package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.MovieRequest;
import com.moviebooking.movie_booking_monolith.dto.response.MovieResponse;
import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.mapper.MovieMapper;
import com.moviebooking.movie_booking_monolith.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


import java.util.List;

@Service
@Transactional
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "movies:all")
    public List<MovieResponse> getAll() {
        List<Movie> movies = movieRepository.findAll();
        return movieMapper.toResponseList(movies);
    }

    @Transactional(readOnly = true)
    public MovieResponse getById(Long id) {
        Movie movie = findMovieById(id);
        return movieMapper.toResponse(movie);
    }

    @CacheEvict(value = "movies:all", allEntries = true)
    public MovieResponse create(MovieRequest request) {
        Movie movie = movieMapper.toEntity(request);
        Movie saved = movieRepository.save(movie);
        return movieMapper.toResponse(saved);
    }

    public MovieResponse update(Long id, MovieRequest request) {
        Movie movie = findMovieById(id);
        movieMapper.updateEntity(movie, request);
        Movie updated = movieRepository.save(movie);
        return movieMapper.toResponse(updated);
    }

    public void delete(Long id) {
        Movie movie = findMovieById(id);
        movieRepository.delete(movie);
    }

    private Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> getPage(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movieMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> searchByName(String name, Pageable pageable) {
        return movieRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(movieMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MovieResponse> searchByGenre(String genre, Pageable pageable) {
        return movieRepository.findByGenreIgnoreCase(genre, pageable)
                .map(movieMapper::toResponse);
    }
}
