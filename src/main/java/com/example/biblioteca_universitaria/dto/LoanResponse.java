package com.example.biblioteca_universitaria.dto;

import com.example.biblioteca_universitaria.domain.enums.LoanStatus;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long userId,
        String userEmail,
        Long bookId,
        String bookTitle,
        LocalDate dataEmprestimo,
        LocalDate dataDevolucaoPrevista,
        LocalDate dataDevolucaoReal,
        LoanStatus status
) {}
