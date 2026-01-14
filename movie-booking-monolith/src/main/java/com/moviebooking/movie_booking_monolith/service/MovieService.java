package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie updateMovie(Long id, Movie movieDetails) {
        Movie movie = getMovieById(id);
        movie.setName(movieDetails.getName());
        movie.setDuration(movieDetails.getDuration());
        movie.setRating(movieDetails.getRating());
        movie.setGenre(movieDetails.getGenre());
        movie.setLanguage(movieDetails.getLanguage());
        movie.setReleaseDate(movieDetails.getReleaseDate());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);
        movieRepository.delete(movie);
    }
}
