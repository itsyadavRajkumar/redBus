package com.redBus.service.impl;

import com.redBus.enums.SeatStatus;
import com.redBus.model.Seat;
import com.redBus.repository.SeatRepo;
import com.redBus.service.SeatReleaseScheduler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SeatReleaseSchedulerImpl implements SeatReleaseScheduler {

    private final SeatRepo seatRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SeatReleaseSchedulerImpl(SeatRepo seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    @Scheduled(fixedRate = 60000)
    @SchedulerLock(name = "seatReleaseJob", lockAtMostFor = "55s")
    @Transactional
    public void releaseExpiredSeats() {

        Page<Seat> page;

        do {
            page = seatRepository.findExpiredSeatsForUpdate(
                    SeatStatus.HELD,
                    LocalDateTime.now(),
                    PageRequest.of(0, 50));

            for (Seat seat : page.getContent()) {

                if (seat.getStatus() == SeatStatus.HELD &&
                        seat.getLockExpiryTime().isBefore(LocalDateTime.now())) {

                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.setLockedBy(null);
                    seat.setLockExpiryTime(null);
                }
            }

            entityManager.flush();
            entityManager.clear();

        } while (!page.isEmpty());
    }
}