package com.redBus.model;

import com.redBus.enums.BerthType;
import com.redBus.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(
        indexes = {
                @Index(name = "idx_seat_status_expiry", columnList = "status, lockExpiryTime")
        }
)
@Entity
@Getter
@Setter
public class Seat {

    @Id
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private BerthType berthType;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "locked_by_user")
    private Users lockedBy;

    private LocalDateTime lockExpiryTime;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    public Seat() {
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

}