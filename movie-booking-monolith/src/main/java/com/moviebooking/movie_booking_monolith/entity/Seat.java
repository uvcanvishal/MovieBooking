package com.moviebooking.movie_booking_monolith.entity;

import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String seatNumber;

    @ManyToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    // DAY 7: Lock metadata
    @Column(name = "lock_expiry_time")
    private LocalDateTime lockExpiryTime;

    @Column(name = "locked_by_user_id")
    private Long lockedByUserId;
}
