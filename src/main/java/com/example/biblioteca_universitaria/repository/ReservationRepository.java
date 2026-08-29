package com.example.biblioteca_universitaria.repository;

import com.example.biblioteca_universitaria.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
