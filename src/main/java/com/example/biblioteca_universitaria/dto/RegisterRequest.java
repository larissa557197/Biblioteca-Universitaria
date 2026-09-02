// Define o pacote onde os Data Transfer Objects (DTOs) estão localizados
package com.example.biblioteca_universitaria.dto;

// Importações das anotações da biblioteca Lombok para geração automática de métodos
import lombok.Getter;
import lombok.Setter;

// Anotação do Lombok que gera automaticamente os métodos de leitura (getters) para todos os campos
@Getter
// Anotação do Lombok que gera automaticamente os métodos de escrita (setters) para todos os campos
@Setter
public class RegisterRequest {

    // Nome completo do usuário enviado na requisição de cadastro
    private String nome;
    // Endereço de e-mail único do usuário (usado como login)
    private String email;
    // Senha em texto puro enviada pelo usuário (será criptografada no Controller)
    private String senha;
    // Perfil/Regra de acesso do usuário no sistema ("ADMIN" ou "STUDENT")
    private String role; // "ADMIN" ou "STUDENT"

}
