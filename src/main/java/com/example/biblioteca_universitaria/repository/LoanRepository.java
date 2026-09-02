package com.example.biblioteca_universitaria.repository;

import com.example.biblioteca_universitaria.domain.Loan;
import com.example.biblioteca_universitaria.domain.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Lista todos os empréstimos de um usuário específico
    List<Loan> findByBookId(Long bookId);

    // Lista todos os empréstimos de um usuário específico
    List<Loan> findByStatus(LoanStatus status);

    // Verifica se o usuário já possui um empréstimo em aberto para o mesmo livro
    boolean existsByUserIdAndBookIdAndStatus(Long userId, Long bookId, LoanStatus status);
}
