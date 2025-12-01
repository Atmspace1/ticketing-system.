package com.ticketing.service;

import com.ticketing.model.Seat;
import com.ticketing.repository.SeatRepository;
import com.ticketing.strategy.PricingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BookingService - Implement IBookable Interface
 * ใช้หลักการ OOP ทั้ง 4 ข้อ:
 * 1. Encapsulation - ซ่อน logic การจองไว้ใน Service
 * 2. Abstraction - Implement IBookable Interface
 * 3. Inheritance - ใช้ SeatBase hierarchy
 * 4. Polymorphism - ใช้ PricingStrategy
 */
@Service
@Transactional
public class BookingService implements IBookable {

    private final SeatRepository seatRepository;

    // Map สำหรับเก็บ Pricing Strategy ที่มี (Polymorphism)
    private static final Map<String, PricingStrategy> strategies = Map.of(
            "regular", new RegularPricingStrategy(),
            "member", new MemberDiscountStrategy(),
            "student", new StudentDiscountStrategy(),
            "senior", new SeniorDiscountStrategy()
    );

    @Autowired
    public BookingService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * จองชั่วคราว (Hold) - Encapsulation
     */
    @Override
    public boolean reserve(Long seatId, int minutes) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

            // ใช้ method ของ Seat class (Encapsulation)
            seat.holdSeat(minutes);
            seatRepository.save(seat);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Reservation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * ยืนยันการจอง
     */
    @Override
    public boolean confirmBooking(Long seatId, String customerName, String customerPhone, LocalDateTime bookingDate) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

            // ใช้ method ของ Seat class (Encapsulation)
            seat.confirmBooking(customerName, customerPhone, bookingDate);
            seatRepository.save(seat);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Confirmation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * ยกเลิกการจอง
     */
    @Override
    public boolean cancelReservation(Long seatId) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

            // ใช้ method ของ Seat class (Encapsulation)
            seat.cancelReservation();
            seatRepository.save(seat);
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Cancellation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * ตรวจสอบสถานะว่าว่างหรือไม่
     */
    @Override
    public boolean isAvailable(Long seatId) {
        Optional<Seat> seat = seatRepository.findById(seatId);
        return seat.map(s -> s.getCurrentStatus() == Seat.SeatStatus.AVAILABLE).orElse(false);
    }

    /**
     * คำนวณราคาสุทธิ (Polymorphism)
     */
    @Override
    public double calculatePrice(Long seatId, String customerType) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

        PricingStrategy strategy = strategies.getOrDefault(customerType.toLowerCase(), new RegularPricingStrategy());

        // ใช้ Polymorphism: เรียก method calculatePrice() จาก Strategy ที่เลือก
        double finalPrice = strategy.calculatePrice(seat.getBasePrice());
        return Math.round(finalPrice * 100.0) / 100.0;
    }

    // =====================================
    // Helper Methods for Controller
    // =====================================

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public List<Seat> getAvailableSeats() {
        return seatRepository.findByCurrentStatus(Seat.SeatStatus.AVAILABLE);
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public List<Seat> getSeatsByZone(String zone) {
        return seatRepository.findByZone(zone);
    }

    // Scheduled job สำหรับปล่อยที่นั่งที่ Hold หมดเวลา
    public void releaseExpiredHolds() {
        List<Seat> expiredSeats = seatRepository.findExpiredHoldSeats();
        for (Seat seat : expiredSeats) {
            seat.checkAndReleaseExpiredHold();
            seatRepository.save(seat);
        }
    }
}

/**
 * Inner classes for PricingStrategy (Polymorphism)
 */
class RegularPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double basePrice) { return basePrice; }
    @Override
    public String getStrategyName() { return "Regular"; }
    @Override
    public double getDiscountPercentage() { return 0.0; }
}

class MemberDiscountStrategy implements PricingStrategy {
    private static final double DISCOUNT = 0.10;
    @Override
    public double calculatePrice(double basePrice) { return basePrice * (1 - DISCOUNT); }
    @Override
    public String getStrategyName() { return "Member"; }
    @Override
    public double getDiscountPercentage() { return DISCOUNT * 100; }
}

class StudentDiscountStrategy implements PricingStrategy {
    private static final double DISCOUNT = 0.15;
    @Override
    public double calculatePrice(double basePrice) { return basePrice * (1 - DISCOUNT); }
    @Override
    public String getStrategyName() { return "Student"; }
    @Override
    public double getDiscountPercentage() { return DISCOUNT * 100; }
}

class SeniorDiscountStrategy implements PricingStrategy {
    private static final double DISCOUNT = 0.20;
    @Override
    public double calculatePrice(double basePrice) { return basePrice * (1 - DISCOUNT); }
    @Override
    public String getStrategyName() { return "Senior"; }
    @Override
    public double getDiscountPercentage() { return DISCOUNT * 100; }
}