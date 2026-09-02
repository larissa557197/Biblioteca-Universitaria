// Define o pacote onde os Data Transfer Objects (DTOs) estão localizados
package com.example.biblioteca_universitaria.dto;

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

    public BookResponse(Long id, String titulo, String autor, String isbn, Integer totalExemplares, Integer exemplaresDisponiveis) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.totalExemplares = totalExemplares;
        this.exemplaresDisponiveis = exemplaresDisponiveis;
    }
}
