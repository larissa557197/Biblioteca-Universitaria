package com.example.biblioteca_universitaria.service;

import com.example.biblioteca_universitaria.domain.Book;
import com.example.biblioteca_universitaria.domain.Loan;
import com.example.biblioteca_universitaria.domain.User;
import com.example.biblioteca_universitaria.domain.enums.LoanStatus;
import com.example.biblioteca_universitaria.dto.LoanRequest;
import com.example.biblioteca_universitaria.dto.LoanResponse;
import com.example.biblioteca_universitaria.repository.BookRepository;
import com.example.biblioteca_universitaria.repository.LoanRepository;
import com.example.biblioteca_universitaria.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoanResponse createLoan(LoanRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + request.userId()));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com ID: " + request.bookId()));

        // 1. Validação de estoque
        if (book.getTotalExemplares() <= 0) {
            throw new RuntimeException("Não há exemplares disponíveis para o livro: " + book.getTitulo());
        }

        // 2. Validação de empréstimo em aberto para o mesmo livro
        boolean hasActiveLoan = loanRepository.existsByUsuarioIdAndLivroIdAndStatus(user.getId(), book.getId(), LoanStatus.OPEN);
        if (hasActiveLoan) {
            throw new RuntimeException("O usuário já possui um empréstimo em aberto deste livro.");
        }

        // 3. Atualizar estoque do livro
        book.setTotalExemplares(book.getTotalExemplares() - 1);
        bookRepository.save(book);

        // 4. Criar empréstimo (Devolução prevista para 14 dias)
        Loan loan = Loan.builder()
                .usuario(user)
                .livro(book)
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .status(LoanStatus.OPEN)
                .build();

        Loan savedLoan = loanRepository.save(loan);
        return toResponse(savedLoan);
    }

    @Transactional
    public LoanResponse returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado com ID: " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new RuntimeException("Este empréstimo já foi devolvido.");
        }

        // 1. Atualizar data de devolução real e status
        loan.setDataDevolucaoReal(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        // 2. Incrementar estoque do livro
        Book book = loan.getLivro();
        book.setTotalExemplares(book.getTotalExemplares() + 1);
        bookRepository.save(book);

        Loan updatedLoan = loanRepository.save(loan);
        return toResponse(updatedLoan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByUser(Long userId) {
        return loanRepository.findByUsuarioId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUsuario().getId(),
                loan.getUsuario().getEmail(),
                loan.getLivro().getId(),
                loan.getLivro().getTitulo(),
                loan.getDataEmprestimo(),
                loan.getDataDevolucaoPrevista(),
                loan.getDataDevolucaoReal(),
                loan.getStatus()
        );
    }
}