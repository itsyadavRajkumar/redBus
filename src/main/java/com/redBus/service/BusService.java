package com.redBus.service;

import com.redBus.payload.BusRequestDto;

public interface BusService {
    void createBus(BusRequestDto busRequestDto);

    void holdSeat(String seatNumber, Long userId);

    void confirmBooking(String seatNumber, Long userId);
}
