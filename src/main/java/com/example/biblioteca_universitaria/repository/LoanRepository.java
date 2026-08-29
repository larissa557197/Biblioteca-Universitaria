package com.example.biblioteca_universitaria.repository;

import com.example.biblioteca_universitaria.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
