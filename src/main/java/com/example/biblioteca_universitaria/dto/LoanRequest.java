package com.example.biblioteca_universitaria.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long userId,

        @NotNull(message = "O ID do livro é obrigatório")
        Long bookId
) {}