package com.example.biblioteca_universitaria.domain;

import com.example.biblioteca_universitaria.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book livro;

    @Column(nullable = false)
    private LocalDate dataReserva;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

}
