package com.moviebooking.movie_booking_monolith.entity;

import com.moviebooking.movie_booking_monolith.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who booked
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // which show
    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    // which seats
    @ManyToMany
    @JoinTable(
            name = "booking_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<Seat> seats;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
}
