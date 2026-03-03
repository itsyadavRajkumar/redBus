package com.redBus.repository;

import com.redBus.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepo extends JpaRepository<Bus, Long> {
    boolean existsByBusNumber(String busNumber);
}