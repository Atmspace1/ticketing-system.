package com.ticketing.repository;

import com.ticketing.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByZone(String zone);
    List<Seat> findByCurrentStatus(Seat.SeatStatus status);

    // ค้นหาที่นั่งที่จองค้างไว้ (HOLD) และหมดเวลาแล้ว
    @Query("SELECT s FROM Seat s WHERE s.currentStatus = 'HOLD' AND s.holdExpiryTime < CURRENT_TIMESTAMP")
    List<Seat> findExpiredHoldSeats();
}