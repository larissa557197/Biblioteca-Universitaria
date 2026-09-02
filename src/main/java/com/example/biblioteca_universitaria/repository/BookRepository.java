// Define o pacote onde as interfaces de acesso ao banco de dados estão localizadas
package com.example.biblioteca_universitaria.repository;

// Importação da entidade Book
import com.example.biblioteca_universitaria.domain.Book;
// Importação da interface base do Spring Data JPA
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

    // Verifica no banco se já existe algum livro cadastrado com o ISBN informado
    boolean existsByIsbn(String isbn);
}
