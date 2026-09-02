// Define o pacote onde as classes de serviço e regras de negócio estão localizadas
package com.example.biblioteca_universitaria.service;

import com.example.biblioteca_universitaria.domain.Book;
import com.example.biblioteca_universitaria.dto.BookRequest;
import com.example.biblioteca_universitaria.dto.BookResponse;
import com.example.biblioteca_universitaria.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Injeção de dependência via construtor
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Salva um novo livro garantindo que o ISBN seja único
    @Transactional
    public BookResponse cadastrar (BookRequest bookRequest) {
        if (bookRepository.existsByIsbn(bookRequest.getIsbn())){
            throw new IllegalArgumentException("Já existe um livro cadastrado com este ISBN");
        }

        Book book = Book.builder()
                .titulo(bookRequest.getTitulo())
                .autor(bookRequest.getAutor())
                .isbn(bookRequest.getIsbn())
                .totalExemplares(bookRequest.getTotalExemplares())
                // No cadastro inicial, todos os exemplares estão disponíveis
                .exemplaresDisponiveis(bookRequest.getTotalExemplares())
                .build();

        Book livrosSalvo = bookRepository.save(book);
        return new BookResponse(livrosSalvo);
    }

    // Retorna a lista de todos os livros
    @Transactional(readOnly = true)
    public List<BookResponse> listarTodos() {
        return bookRepository.findAll()
                .stream()
                .map(BookResponse::new)
                .collect(Collectors.toList());
    }

    // Busca um livro pelo ID
    @Transactional(readOnly = true)
    public BookResponse buscarPorId(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado com o ID: " + id));
        return new BookResponse(book);
    }

    // Atualiza os dados de um livro existente
    @Transactional
    public BookResponse atualizar(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado com o ID: " + id));

        // Se alterou o ISBN, verifica se o novo ISBN já pertence a outro livro
        if(!book.getIsbn().equals(bookRequest.getIsbn()) && bookRepository.existsByIsbn(bookRequest.getIsbn())){
            throw new IllegalArgumentException("Já existe outro livro cadastrado com este ISBN");
        }

        // Calcula a diferença no total de exemplares para ajustar os disponiveis
        int diferenca = bookRequest.getTotalExemplares() - book.getTotalExemplares();
        int novosDisponiveis = book.getExemplaresDisponiveis() + diferenca;

        if (novosDisponiveis < 0){
            throw new IllegalArgumentException("Não é possível reduzir o total abaixo do número de exemplares emprestados no momento");
        }

        book.setTitulo(bookRequest.getTitulo());
        book.setAutor(bookRequest.getAutor());
        book.setIsbn(bookRequest.getIsbn());
        book.setTotalExemplares(bookRequest.getTotalExemplares());
        book.setExemplaresDisponiveis(bookRequest.getTotalExemplares());

        Book livroAtualizado = bookRepository.save(book);
        return new BookResponse(livroAtualizado);
    }

    // Remove um livro do banco pelo ID
    @Transactional
    public void deletar(Long id) {
        if (bookRepository.existsById(id)){
            throw new RuntimeException("Livro não encontrado com o ID: " + id);
        }
        bookRepository.deleteById(id);
    }
}
