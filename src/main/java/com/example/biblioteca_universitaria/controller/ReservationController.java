package com.example.biblioteca_universitaria.controller;

import com.example.biblioteca_universitaria.domain.Book;
import com.example.biblioteca_universitaria.domain.Reservation;
import com.example.biblioteca_universitaria.domain.User;
import com.example.biblioteca_universitaria.domain.enums.ReservationStatus;
import com.example.biblioteca_universitaria.repository.BookRepository;
import com.example.biblioteca_universitaria.repository.ReservationRepository;
import com.example.biblioteca_universitaria.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public ReservationController(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Reservation> listar() {
        return reservationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> buscarPorId(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestParam Long usuarioId,
            @RequestParam Long livroId
    ) {
        User usuario = userRepository.findById(usuarioId)
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        Book livro = bookRepository.findById(livroId)
                .orElse(null);
        if (livro == null) {
            return ResponseEntity.badRequest().body("Livro não encontrado");
        }

        Reservation reserva = Reservation.builder()
                .usuario(usuario)
                .livro(livro)
                .dataReserva(LocalDate.now())
                .status(ReservationStatus.ACTIVE)
                .build();

        Reservation salvo = reservationRepository.save(reserva);

        return ResponseEntity.ok(salvo);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reserva -> {
                    reserva.setStatus(ReservationStatus.CANCELLED);
                    Reservation salvo = reservationRepository.save(reserva);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!reservationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        reservationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}