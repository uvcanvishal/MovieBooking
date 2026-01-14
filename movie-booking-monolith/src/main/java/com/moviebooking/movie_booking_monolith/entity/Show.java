package com.moviebooking.movie_booking_monolith.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many shows can be for same movie
    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // Many shows can be in same theater
    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private LocalDateTime showTime;

    @Column(nullable = false)
    private Double price;
}
