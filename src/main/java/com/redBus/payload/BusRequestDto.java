package com.redBus.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusRequestDto {
    private String busNumber;
    private int totalSeats;
}