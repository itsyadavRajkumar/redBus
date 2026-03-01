package com.redBus.service;

public interface BusService {
    void holdSeat(String seatNumber, Long userId);

    void confirmBooking(String seatNumber, Long userId);
}
