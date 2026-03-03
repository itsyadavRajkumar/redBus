package com.redBus.service.impl;

import com.redBus.enums.PaymentStatus;
import com.redBus.enums.SeatStatus;
import com.redBus.model.Booking;
import com.redBus.model.Seat;
import com.redBus.model.Users;
import com.redBus.repository.BookingRepo;
import com.redBus.repository.SeatRepo;
import com.redBus.repository.UsersRepo;
import com.redBus.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;
    private final SeatRepo seatRepo;
    private final UsersRepo usersRepo;

    public BookingServiceImpl(
            BookingRepo bookingRepo,
            SeatRepo seatRepo,
            UsersRepo usersRepo) {

        this.bookingRepo = bookingRepo;
        this.seatRepo = seatRepo;
        this.usersRepo = usersRepo;
    }

    @Transactional
    @Override
    public String bookSeat(String seatNumber, Long userId) {

        Seat seat = seatRepo.findAndLockSeat(seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate ownership
        if (seat.getLockedBy() == null ||
                !seat.getLockedBy().getId().equals(userId)) {
            throw new RuntimeException("Seat not locked by this user");
        }

        if (seat.getLockExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Seat lock expired");
        }

        // Create Booking
        Booking booking = new Booking();
        booking.setPnr(generatePNR());
        booking.setUser(user);
        booking.setSeats(List.of(seat));
        booking.setPaymentStatus(PaymentStatus.SUCCESS);
        booking.setCreatedAt(LocalDateTime.now());

        seat.setStatus(SeatStatus.BOOKED);
        seat.setLockedBy(null);
        seat.setLockExpiryTime(null);

        bookingRepo.save(booking);

        return booking.getPnr();
    }

    private String generatePNR() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}