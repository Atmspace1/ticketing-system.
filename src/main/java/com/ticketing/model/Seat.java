package com.ticketing.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "seats")
public class Seat {
    public enum SeatStatus { AVAILABLE, HOLD, BOOKED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seatNumber;
    private String zone;
    private int capacity;
    private double basePrice;

    @Enumerated(EnumType.STRING)
    private SeatStatus currentStatus = SeatStatus.AVAILABLE;

    private LocalDateTime holdExpiryTime;
    private String customerName;
    private String customerPhone;
    private LocalDateTime bookingDate;

    public Seat(String seatNumber, String zone, int capacity, double basePrice) {
        this.seatNumber = seatNumber;
        this.zone = zone;
        this.capacity = capacity;
        this.basePrice = basePrice;
    }

    // Encapsulation Logic: ควบคุมการเปลี่ยนสถานะผ่าน Method
    public void holdSeat(int minutes) {
        if (this.currentStatus != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("Seat is not available");
        }
        this.currentStatus = SeatStatus.HOLD;
        this.holdExpiryTime = LocalDateTime.now().plusMinutes(minutes);
    }

    public void confirmBooking(String name, String phone, LocalDateTime date) {
        if (this.currentStatus != SeatStatus.HOLD) {
            throw new IllegalStateException("Seat must be on hold first");
        }
        this.currentStatus = SeatStatus.BOOKED;
        this.customerName = name;
        this.customerPhone = phone;
        this.bookingDate = date;
        this.holdExpiryTime = null;
    }

    public void cancelReservation() {
        this.currentStatus = SeatStatus.AVAILABLE;
        this.customerName = null;
        this.customerPhone = null;
        this.bookingDate = null;
        this.holdExpiryTime = null;
    }

    public void checkAndReleaseExpiredHold() {
        if (this.currentStatus == SeatStatus.HOLD &&
                this.holdExpiryTime != null &&
                LocalDateTime.now().isAfter(this.holdExpiryTime)) {
            cancelReservation();
        }
    }
}