package com.ticketing.controller;

import com.ticketing.model.Seat;
import com.ticketing.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * BookingController - RESTful API Controller
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // อนุญาตให้ Frontend (HTML) เรียกใช้ได้
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // =====================================
    // Seat Management Endpoints
    // =====================================

    @GetMapping("/seats")
    public List<Seat> getAllSeats() {
        return bookingService.getAllSeats();
    }

    @GetMapping("/seats/{id}")
    public ResponseEntity<Seat> getSeatById(@PathVariable Long id) {
        Optional<Seat> seat = bookingService.getSeatById(id);
        return seat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/seats/zone/{zone}")
    public List<Seat> getSeatsByZone(@PathVariable String zone) {
        return bookingService.getSeatsByZone(zone);
    }

    @GetMapping("/seats/available")
    public List<Seat> getAvailableSeats() {
        return bookingService.getAvailableSeats();
    }

    // =====================================
    // Booking Endpoints
    // =====================================

    // POST /api/bookings/reserve
    @PostMapping("/bookings/reserve")
    public ResponseEntity<Map<String, Object>> reserveSeat(@RequestBody ReserveRequest request) {
        boolean success = bookingService.reserve(request.getSeatId(), request.getMinutes());
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("message", "Seat " + request.getSeatId() + " successfully held for " + request.getMinutes() + " minutes.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to hold seat " + request.getSeatId() + ". It might be already booked or held.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // POST /api/bookings/confirm
    @PostMapping("/bookings/confirm")
    public ResponseEntity<Map<String, Object>> confirmBooking(@RequestBody ConfirmRequest request) {
        boolean success = bookingService.confirmBooking(
                request.getSeatId(),
                request.getCustomerName(),
                request.getCustomerPhone(),
                request.getBookingDate() // LocalDateTime
        );
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("message", "Booking confirmed for seat " + request.getSeatId() + ".");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to confirm booking for seat " + request.getSeatId() + ". It must be on hold first.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // POST /api/bookings/cancel
    @PostMapping("/bookings/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@RequestBody CancelRequest request) {
        boolean success = bookingService.cancelReservation(request.getSeatId());
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("message", "Reservation cancelled for seat " + request.getSeatId() + ".");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Failed to cancel reservation for seat " + request.getSeatId() + ".");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // GET /api/bookings/price
    @GetMapping("/bookings/price")
    public ResponseEntity<Map<String, Object>> calculatePrice(
            @RequestParam Long seatId,
            @RequestParam String customerType
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            double finalPrice = bookingService.calculatePrice(seatId, customerType);
            response.put("finalPrice", finalPrice);
            response.put("customerType", customerType);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // =====================================
    // Request DTOs (Data Transfer Objects)
    // ใช้รับข้อมูลจาก JSON request
    // =====================================

    static class ReserveRequest {
        private Long seatId;
        private int minutes;

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
        public int getMinutes() { return minutes; }
        public void setMinutes(int minutes) { this.minutes = minutes; }
    }

    static class ConfirmRequest {
        private Long seatId;
        private String customerName;
        private String customerPhone;
        private LocalDateTime bookingDate;

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
        public LocalDateTime getBookingDate() { return bookingDate; }
        public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    }

    static class CancelRequest {
        private Long seatId;

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
    }
}