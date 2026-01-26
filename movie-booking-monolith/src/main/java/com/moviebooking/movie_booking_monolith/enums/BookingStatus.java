package com.moviebooking.movie_booking_monolith.enums;

public enum BookingStatus {
    PENDING,        // seats locked, waiting for payment
    CONFIRMED,      // payment success
    CANCELLED,      // user/business cancelled before or after payment
    FAILED,         // payment failed
    EXPIRED         // lock/booking expired without payment (optional for later)
}

