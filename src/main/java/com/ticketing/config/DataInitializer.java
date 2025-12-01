package com.ticketing.config;

import com.ticketing.model.Seat;
import com.ticketing.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - สร้างข้อมูลตัวอย่างในฐานข้อมูล
 * จะทำงานทีเดียวตอน Application เริ่มต้น
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void run(String... args) throws Exception {
        // ตรวจสอบว่ามีข้อมูลแล้วหรือไม่
        if (seatRepository.count() > 0) {
            System.out.println("✓ Data already initialized. Skipping...");
            return;
        }

        System.out.println("⚙ Initializing sample data...");
        // VIP Zone
        seatRepository.save(new Seat("T01", "VIP", 4, 500.0));
        seatRepository.save(new Seat("T02", "VIP", 4, 500.0));
        seatRepository.save(new Seat("T03", "VIP", 6, 700.0));

        // Zone A
        seatRepository.save(new Seat("T04", "A", 4, 300.0));
        seatRepository.save(new Seat("T05", "A", 4, 300.0));
        seatRepository.save(new Seat("T06", "A", 4, 300.0));

        // Zone B
        seatRepository.save(new Seat("T07", "B", 6, 400.0));
        seatRepository.save(new Seat("T08", "B", 6, 400.0));
        seatRepository.save(new Seat("T09", "B", 8, 600.0));

        // Zone C
        seatRepository.save(new Seat("T10", "C", 2, 200.0));
        seatRepository.save(new Seat("T11", "C", 2, 200.0));
        seatRepository.save(new Seat("T12", "C", 2, 200.0));

        long count = seatRepository.count();
        System.out.println("✓ Sample data initialized successfully!");
        System.out.println("✓ Total seats created: " + count);

        // แสดงสถิติ
        System.out.println("\n📊 Seat Statistics:");
        System.out.println("  VIP Zone: " + seatRepository.findByZone("VIP").size() + " seats");
        System.out.println("  Zone A: " + seatRepository.findByZone("A").size() + " seats");
        System.out.println("  Zone B: " + seatRepository.findByZone("B").size() + " seats");
        System.out.println("  Zone C: " + seatRepository.findByZone("C").size() + " seats");
    }
}