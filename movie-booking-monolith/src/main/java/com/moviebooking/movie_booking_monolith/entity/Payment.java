package com.moviebooking.movie_booking_monolith.entity;

import com.moviebooking.movie_booking_monolith.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Razorpay / generic gateway payment id
    @Column(name = "gateway_payment_id")
    private String gatewayPaymentId;

    @Column(name = "gateway_order_id")
    private String gatewayOrderId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
