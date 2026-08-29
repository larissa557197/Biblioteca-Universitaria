package com.example.biblioteca_universitaria.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String nome;
    private String email;
    private String senha;
    private String role; // "ADMIN" ou "STUDENT"

}
