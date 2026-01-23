package com.moviebooking.movie_booking_monolith.service;

import com.moviebooking.movie_booking_monolith.dto.request.SeatLockRequest;
import com.moviebooking.movie_booking_monolith.dto.response.SeatLockResponse;
import com.moviebooking.movie_booking_monolith.entity.Seat;
import com.moviebooking.movie_booking_monolith.entity.Show;
import com.moviebooking.movie_booking_monolith.enums.SeatStatus;
import com.moviebooking.movie_booking_monolith.exception.BadRequestException;
import com.moviebooking.movie_booking_monolith.exception.ResourceNotFoundException;
import com.moviebooking.movie_booking_monolith.repository.SeatRepository;
import com.moviebooking.movie_booking_monolith.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeatLockService {

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    public SeatLockService(SeatRepository seatRepository, ShowRepository showRepository) {
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public SeatLockResponse lockSeats(Long userId, SeatLockRequest request) {
        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> new ResourceNotFoundException("Show", "id", request.showId()));

        Long theaterId = show.getTheater().getId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(request.lockDurationMinutes());

        // Find seats (same logic as your BookingService)
        List<Seat> seats = seatRepository.findByTheaterIdAndSeatNumberIn(theaterId, request.seatNumbers());
        if (seats.size() != request.seatNumbers().size()) {
            throw new BadRequestException("One or more seats not found");
        }

        // Validate: available OR locked by THIS user and not expired
        for (Seat seat : seats) {
            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " already booked");
            }

            if (seat.getStatus() == SeatStatus.LOCKED
                    && seat.getLockExpiryTime() != null
                    && seat.getLockExpiryTime().isAfter(now)
                    && !seat.getLockedByUserId().equals(userId)) {
                throw new BadRequestException("Seat " + seat.getSeatNumber() + " locked by another user");
            }
        }

        // Lock them
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
            seat.setLockExpiryTime(expiryTime);
            seat.setLockedByUserId(userId);
        }
        seatRepository.saveAll(seats);

        return new SeatLockResponse(
                seats.stream().map(Seat::getSeatNumber).toList(),
                expiryTime,
                "Seats locked successfully"
        );
    }

    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        List<Seat> expired = seatRepository.findByStatusAndLockExpiryTimeBefore(SeatStatus.LOCKED, now);
        expired.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockExpiryTime(null);
            seat.setLockedByUserId(null);
        });
        if (!expired.isEmpty()) {
            seatRepository.saveAll(expired);
        }
    }

    @Transactional
    public void releaseUserLocks(Long userId, Long theaterId, List<String> seatNumbers) {
        List<Seat> seats = seatRepository.findByLockedByUserIdAndTheaterIdAndSeatNumberIn(userId, theaterId, seatNumbers);
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLockExpiryTime(null);
            seat.setLockedByUserId(null);
        });
        if (!seats.isEmpty()) {
            seatRepository.saveAll(seats);
        }
    }
}
