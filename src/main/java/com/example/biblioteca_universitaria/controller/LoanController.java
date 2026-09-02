package com.example.biblioteca_universitaria.controller;

import com.example.biblioteca_universitaria.domain.Book;
import com.example.biblioteca_universitaria.domain.Loan;
import com.example.biblioteca_universitaria.domain.User;
import com.example.biblioteca_universitaria.domain.enums.LoanStatus;
import com.example.biblioteca_universitaria.repository.BookRepository;
import com.example.biblioteca_universitaria.repository.LoanRepository;
import com.example.biblioteca_universitaria.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
public class LoanController {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LoanController(
            LoanRepository loanRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Loan> listar() {
        return loanRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> buscarPorId(@PathVariable Long id) {
        return loanRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(
            @RequestParam Long usuarioId,
            @RequestParam Long livroId,
            @RequestParam(required = false) Integer diasDeEmprestimo
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

        if (livro.getExemplaresDisponiveis() <= 0) {
            return ResponseEntity.badRequest().body("Não há exemplares disponíveis para empréstimo");
        }

        LocalDate hoje = LocalDate.now();
        int dias = diasDeEmprestimo != null ? diasDeEmprestimo : 7;
        LocalDate dataPrevista = hoje.plusDays(dias);

        Loan emprestimo = Loan.builder()
                .usuario(usuario)
                .livro(livro)
                .dataEmprestimo(hoje)
                .dataDevolucaoPrevista(dataPrevista)
                .status(LoanStatus.OPEN)
                .build();

        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() - 1);
        bookRepository.save(livro);

        Loan salvo = loanRepository.save(emprestimo);

        return ResponseEntity.ok(salvo);
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        return loanRepository.findById(id)
                .map(emprestimo -> {
                    if (emprestimo.getStatus() == LoanStatus.RETURNED) {
                        return ResponseEntity.badRequest().body("Empréstimo já devolvido");
                    }

                    emprestimo.setDataDevolucaoReal(LocalDate.now());

                    if (emprestimo.getDataDevolucaoReal().isAfter(emprestimo.getDataDevolucaoPrevista())) {
                        emprestimo.setStatus(LoanStatus.LATE);
                    } else {
                        emprestimo.setStatus(LoanStatus.RETURNED);
                    }

                    Book livro = emprestimo.getLivro();
                    livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() + 1);
                    bookRepository.save(livro);

                    Loan salvo = loanRepository.save(emprestimo);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!loanRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        loanRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
