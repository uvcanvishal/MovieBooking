package com.moviebooking.movie_booking_monolith.mapper;

import com.moviebooking.movie_booking_monolith.dto.request.MovieRequest;
import com.moviebooking.movie_booking_monolith.dto.response.MovieResponse;
import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.enums.Genre;
import com.moviebooking.movie_booking_monolith.enums.Language;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequest request) {
        Movie movie = new Movie();
        movie.setName(request.getName());
        movie.setDuration(BigDecimal.valueOf(request.getDuration()));
        movie.setRating(request.getRating());
        movie.setGenre(String.valueOf(request.getGenre()));
        movie.setLanguage(String.valueOf(request.getLanguage()));
        movie.setReleaseDate(request.getReleaseDate());
        return movie;
    }

    public void updateEntity(Movie movie, MovieRequest request) {
        movie.setName(request.getName());
        movie.setDuration(BigDecimal.valueOf(request.getDuration()));
        movie.setRating(request.getRating());
        movie.setGenre(String.valueOf(request.getGenre()));
        movie.setLanguage(String.valueOf(request.getLanguage()));
        movie.setReleaseDate(request.getReleaseDate());
    }

    public MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .duration(movie.getDuration())
                .rating(movie.getRating())
                .genre(movie.getGenre() == null ? null : Genre.valueOf(movie.getGenre().toUpperCase()))
                .language(movie.getLanguage() == null ? null : Language.valueOf(movie.getLanguage().toUpperCase()))
                .releaseDate(movie.getReleaseDate())
                .build();
    }

    public List<MovieResponse> toResponseList(List<Movie> movies) {
        return movies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
