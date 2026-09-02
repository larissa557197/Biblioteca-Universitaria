// Define o pacote onde os Data Transfer Objects (DTOs) estão localizados
package com.example.biblioteca_universitaria.dto;

import com.example.biblioteca_universitaria.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private Integer totalExemplares;
    private Integer exemplaresDisponiveis;

    // Construtor utilitário para converter uma entidade Book diretamente em BookResponse
    public BookResponse(Book book){
        this.id = book.getId();
        this.titulo = book.getTitulo();
        this.autor = book.getAutor();
        this.isbn = book.getIsbn();
        this.totalExemplares = book.getTotalExemplares();
        this.exemplaresDisponiveis = book.getExemplaresDisponiveis();
    }
}
