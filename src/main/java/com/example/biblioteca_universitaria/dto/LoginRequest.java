// Define o pacote de Data Transfer Objects (DTOs)
package com.example.biblioteca_universitaria.dto;

// Importações do Lombok para geração automática de métodos auxiliares
import lombok.Getter;
import lombok.Setter;

// Gera automaticamente todos os métodos getters
@Getter
// Gera automaticamente todos os métodos setters
@Setter
public class LoginRequest {

    // E-mail do usuário utilizado como identificador no login
    private String email;
    // Senha em texto claro enviada na requisição para autenticação
    private String senha;
}
