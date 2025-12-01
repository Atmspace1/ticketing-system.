package com.ticketing.config;

import com.ticketing.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * SchedulerConfig - กำหนดค่า Scheduled Tasks
 */
@Configuration
public class SchedulerConfig {

    @Autowired
    private BookingService bookingService;

    /**
     * Scheduled Task: ตรวจสอบและปล่อยที่นั่งที่ Hold หมดเวลา
     * ทำงานทุก 1 นาที (60000 ms)
     */
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHoldSeats() {
        try {
            bookingService.releaseExpiredHolds();
            System.out.println("✓ Checked and released expired hold seats at: " + java.time.LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("✗ Error releasing expired holds: " + e.getMessage());
        }
    }

    /**
     * Scheduled Task: แสดงสถิติระบบ ทุก 5 นาที
     */
    @Scheduled(fixedRate = 300000)
    public void displaySystemStatistics() {
        try {
            long totalSeats = bookingService.getAllSeats().size();
            long availableSeats = bookingService.getAvailableSeats().size();

            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     SYSTEM STATISTICS                  ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Total Seats: " + String.format("%-24s", totalSeats) + "║");
            System.out.println("║  Available: " + String.format("%-26s", availableSeats) + "║");
            System.out.println("║  Occupied: " + String.format("%-27s", (totalSeats - availableSeats)) + "║");
            System.out.println("╚════════════════════════════════════════╝\n");
        } catch (Exception e) {
            System.err.println("✗ Error displaying statistics: " + e.getMessage());
        }
    }
}