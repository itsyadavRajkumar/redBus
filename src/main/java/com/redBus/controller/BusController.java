package com.redBus.controller;

import com.redBus.payload.BusRequestDto;
import com.redBus.service.BusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bus")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createBus(
            @RequestBody BusRequestDto dto) {

        busService.createBus(dto);
        return ResponseEntity.ok("Bus created successfully");
    }

    @PostMapping("/hold")
    public ResponseEntity<String> holdSeat(
            @RequestParam String seatNumber,
            @RequestParam Long userId) {

        busService.holdSeat(seatNumber, userId);
        return ResponseEntity.ok("Seat held successfully");
    }
}
