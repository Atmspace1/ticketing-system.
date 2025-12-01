package com.ticketing.service;

import java.time.LocalDateTime;

public interface IBookable {
    boolean reserve(Long seatId, int minutes);
    boolean confirmBooking(Long seatId, String customerName, String customerPhone, LocalDateTime bookingDate);
    boolean cancelReservation(Long seatId);
    boolean isAvailable(Long seatId);
    double calculatePrice(Long seatId, String customerType);
}