// Define o pacote onde os Data Transfer Objects (DTOs) estão localizados
package com.example.biblioteca_universitaria.dto;

// Importações do Jakarta Validation para validar os campos da requisição
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Importações do Lombok
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Gera os getters, setters e construtores automaticamente
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    // Título do livro é obrigatório
    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    // Autor é obrigatório
    @NotBlank(message = "O autor é obrigatório")
    private String autor;

    // ISBN é obrigatório
    @NotBlank(message = "O ISBN é obrigatório")
    private String isbn;

    // Total de exemplares é obrigatório e deve ser no mínimo 1
    @NotNull(message = "O total de exemplares é obrigatório")
    @Min(value = 1, message = "O número total de exemplares deve ser no mínimo 1")
    private Integer totalExemplares;
}
