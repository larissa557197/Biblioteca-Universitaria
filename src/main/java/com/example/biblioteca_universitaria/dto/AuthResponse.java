// Define o pacote onde os Data Transfer Objects (DTOs) estão localizados
package com.example.biblioteca_universitaria.dto;

// Importações do Lombok para geração automática de construtor e getters
import lombok.AllArgsConstructor;
import lombok.Getter;

// Gera automaticamente os métodos de leitura (getters) para os campos da classe
@Getter
// Gera automaticamente um construtor que aceita todos os campos como argumento
@AllArgsConstructor
public class AuthResponse {

    // Token JWT gerado e retornado na resposta da autenticação
    private String token;
}
