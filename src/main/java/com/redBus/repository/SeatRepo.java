package com.redBus.repository;

import com.redBus.enums.SeatStatus;
import com.redBus.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepo extends JpaRepository<Seat, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber")
    Optional<Seat> findAndLockSeat(@Param("seatNumber") String seatNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT s FROM Seat s
       WHERE s.status = :status
       AND s.lockExpiryTime < :time
       """)
    Page<Seat> findExpiredSeatsForUpdate(
            @Param("status") SeatStatus status,
            @Param("time") LocalDateTime time,
            Pageable pageable);

    List<Seat> findByStatusAndLockExpiryTimeBefore(SeatStatus status, LocalDateTime time);
}
