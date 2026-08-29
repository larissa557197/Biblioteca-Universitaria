package com.example.biblioteca_universitaria.controller;

import com.example.biblioteca_universitaria.domain.Book;
import com.example.biblioteca_universitaria.repository.BookRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public List<Book> listar() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> buscarPorId(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Book livro) {
        if (bookRepository.existsByIsbn(livro.getIsbn())) {
            return ResponseEntity.badRequest().body("ISBN já cadastrado");
        }
        if (livro.getExemplaresDisponiveis() == null) {
            livro.setExemplaresDisponiveis(livro.getTotalExemplares());
        }
        Book salvo = bookRepository.save(livro);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Book livro) {
        return bookRepository.findById(id)
                .map(existente -> {
                    existente.setTitulo(livro.getTitulo());
                    existente.setAutor(livro.getAutor());
                    existente.setIsbn(livro.getIsbn());
                    existente.setTotalExemplares(livro.getTotalExemplares());
                    existente.setExemplaresDisponiveis(livro.getExemplaresDisponiveis());
                    Book salvo = bookRepository.save(existente);
                    return ResponseEntity.ok(salvo);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}