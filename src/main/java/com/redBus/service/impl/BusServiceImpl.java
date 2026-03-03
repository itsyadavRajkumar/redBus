package com.redBus.service.impl;

import com.redBus.enums.BerthType;
import com.redBus.enums.SeatStatus;
import com.redBus.model.Bus;
import com.redBus.model.Seat;
import com.redBus.model.Users;
import com.redBus.payload.BusRequestDto;
import com.redBus.repository.BusRepo;
import com.redBus.repository.SeatRepo;
import com.redBus.repository.UsersRepo;
import com.redBus.service.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BusServiceImpl implements BusService {
    private final SeatRepo seatRepo;
    private final UsersRepo usersRepo;
    private final BusRepo busRepo;

    public BusServiceImpl(SeatRepo seatRepo, UsersRepo usersRepo, BusRepo busRepo) {
        this.seatRepo = seatRepo;
        this.usersRepo = usersRepo;
        this.busRepo = busRepo;
    }

    @Override
    @Transactional
    public void createBus(BusRequestDto dto) {

        if (busRepo.existsByBusNumber(dto.getBusNumber())) {
            throw new RuntimeException("Bus number already exists");
        }

        Bus bus = new Bus();
        bus.setBusNumber(dto.getBusNumber());
        bus.setTotalSeats(dto.getTotalSeats());

        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= dto.getTotalSeats(); i++) {

            Seat seat = new Seat();
            seat.setSeatNumber(dto.getBusNumber() + "_S" + i);
            seat.setStatus(SeatStatus.AVAILABLE);

            // Simple alternating berth logic
            if (i % 2 == 0)
                seat.setBerthType(BerthType.UPPER);
            else
                seat.setBerthType(BerthType.LOWER);

            seat.setBus(bus);
            seats.add(seat);
        }

        bus.setSeats(seats);

        busRepo.save(bus); // Cascade saves seats
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
                seat.getLockExpiryTime().isAfter(LocalDateTime.now()) &&
                !seat.getLockedBy().getId().equals(userId)) {
            throw new RuntimeException("Seat already locked by another user");
        }
        seat.setStatus(SeatStatus.HELD);
        seat.setLockedBy(user);
        seat.setLockExpiryTime(LocalDateTime.now().plusMinutes(5));
    }

    //    @Transactional
//    @Override
//    public void confirmBooking(String seatNumber, Long userId) {
//
//        Seat seat = seatRepo.findById(seatNumber)
//                .orElseThrow(() -> new RuntimeException("Seat not found"));
//
//        Users user = usersRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (seat.getLockedBy() == null || !seat.getLockedBy().getId().equals(userId))
//            throw new RuntimeException("You don't own this seat");
//
//        if (seat.getLockExpiryTime().isBefore(LocalDateTime.now()))
//            throw new RuntimeException("Seat lock expired");
//
//        seat.setStatus(SeatStatus.BOOKED);
//        seat.setLockedBy(null);
//        seat.setLockExpiryTime(null);
//    }
    @Transactional
    @Override
    public void confirmBooking(String seatNumber, Long userId) {

        Seat seat = seatRepo.findAndLockSeat(seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (seat.getLockedBy() == null ||
                !seat.getLockedBy().getId().equals(userId)) {
            throw new RuntimeException("Seat not locked by this user");
        }

        if (seat.getLockExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Seat lock expired");
        }

        seat.setStatus(SeatStatus.BOOKED);
        seat.setLockedBy(null);
        seat.setLockExpiryTime(null);
    }
}
