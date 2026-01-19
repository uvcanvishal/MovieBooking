package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.ShowRequest;
import com.moviebooking.movie_booking_monolith.dto.response.ShowResponse;
import com.moviebooking.movie_booking_monolith.entity.Movie;
import com.moviebooking.movie_booking_monolith.entity.Show;
import com.moviebooking.movie_booking_monolith.entity.Theater;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.mapper.ShowMapper;
import com.moviebooking.movie_booking_monolith.repository.MovieRepository;
import com.moviebooking.movie_booking_monolith.repository.ShowRepository;
import com.moviebooking.movie_booking_monolith.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
@Transactional
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowMapper showMapper;

    @Transactional(readOnly = true)
    public List<ShowResponse> getAll() {
        List<Show> shows = showRepository.findAll();
        return showMapper.toResponseList(shows);
    }

    @Transactional(readOnly = true)
    public ShowResponse getById(Long id) {
        Show show = findShowById(id);
        return showMapper.toResponse(show);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getByMovie(Long movieId) {
        List<Show> shows = showRepository.findByMovieId(movieId);
        return showMapper.toResponseList(shows);
    }

    @Transactional(readOnly = true)
    public List<ShowResponse> getByTheater(Long theaterId) {
        List<Show> shows = showRepository.findByTheaterId(theaterId);
        return showMapper.toResponseList(shows);
    }

    public ShowResponse create(ShowRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie", "id", request.getMovieId()));

        Theater theater = theaterRepository.findById(request.getTheaterId())
                .orElseThrow(() -> new ResourceNotFoundException("Theater", "id", request.getTheaterId()));

        Show show = new Show();
        show.setMovie(movie);
        show.setTheater(theater);
        show.setShowTime(request.getShowTime());
        show.setPrice(request.getPrice());

        Show saved = showRepository.save(show);
        return showMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Show show = findShowById(id);
        showRepository.delete(show);
    }

    private Show findShowById(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<ShowResponse> getPage(Pageable pageable) {
        return showRepository.findAll(pageable)
                .map(showMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ShowResponse> getPageByMovie(Long movieId, Pageable pageable) {
        return showRepository.findByMovieId(movieId, pageable)
                .map(showMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ShowResponse> getPageByTheater(Long theaterId, Pageable pageable) {
        return showRepository.findByTheaterId(theaterId, pageable)
                .map(showMapper::toResponse);
    }
}
