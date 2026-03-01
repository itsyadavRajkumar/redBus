package com.redBus.service.impl;

import com.redBus.enums.SeatStatus;
import com.redBus.model.Seat;
import com.redBus.model.Users;
import com.redBus.repository.SeatRepo;
import com.redBus.repository.UsersRepo;
import com.redBus.service.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BusServiceImpl implements BusService {
    private final SeatRepo seatRepo;
    private final UsersRepo usersRepo;

    public BusServiceImpl(SeatRepo seatRepo, UsersRepo usersRepo) {
        this.seatRepo = seatRepo;
        this.usersRepo = usersRepo;
    }

    @Transactional
    @Override
    public void holdSeat(String seatNumber, Long userId) {

        Seat seat = seatRepo.findAndLockSeat(seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // if seat booked -> stop
        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new RuntimeException("Seat already booked");
        }

        // if someone holding and not expired
        if (seat.getStatus() == SeatStatus.HELD &&
                seat.getLockExpiryTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Seat already locked by another user");
        }

        seat.setStatus(SeatStatus.HELD);
        seat.setLockedBy(user);
        seat.setLockExpiryTime(LocalDateTime.now().plusMinutes(5));
    }

    @Transactional
    @Override
    public void confirmBooking(String seatNumber, Long userId) {

        Seat seat = seatRepo.findById(seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (seat.getLockedBy() == null || !seat.getLockedBy().getId().equals(userId))
            throw new RuntimeException("You don't own this seat");

        if (seat.getLockExpiryTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Seat lock expired");

        seat.setStatus(SeatStatus.BOOKED);
        seat.setLockedBy(null);
        seat.setLockExpiryTime(null);
    }

}
