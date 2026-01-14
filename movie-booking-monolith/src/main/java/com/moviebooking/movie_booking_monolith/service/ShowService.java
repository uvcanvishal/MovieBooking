package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.entity.Show;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.repository.MovieRepository;
import com.moviebooking.movie_booking_monolith.repository.ShowRepository;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    public Show createShow(Long movieId, Long theaterId, Double price, LocalDateTime showTime) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found with id " + movieId));
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new RuntimeException("Theater not found with id " + theaterId));

        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setPrice(price);
        show.setShowTime(showTime);

        return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public List<Show> getShowsByMovie(Long movieId) {
        return showRepository.findByMovieId(movieId);
    }

    public List<Show> getShowsByTheater(Long theaterId) {
        return showRepository.findByTheaterId(theaterId);
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Show not found with id " + id));
    }
}
