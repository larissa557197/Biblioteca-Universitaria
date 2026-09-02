package com.example.biblioteca_universitaria.repository;

import com.example.biblioteca_universitaria.domain.Loan;
import com.example.biblioteca_universitaria.domain.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Lista empréstimos de um determinado usuário
    List<Loan> findByUsuarioId(Long userId);

    // Lista empréstimos de um determinado livro
    List<Loan> findByLivroId(Long bookId);

    // Lista empréstimos filtrando por status (OPEN, RETURNED, LATE)
    List<Loan> findByStatus(LoanStatus status);

    // Verifica se o usuário já possui um empréstimo em aberto para o mesmo livro
    boolean existsByUsuarioIdAndLivroIdAndStatus(Long userId, Long bookId, LoanStatus status);
}