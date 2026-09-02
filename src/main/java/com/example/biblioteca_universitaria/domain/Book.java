// Define o pacote onde as entidades do banco de dados (Domain) estão localizadas
package com.example.biblioteca_universitaria.domain;

// Importações do Jakarta Persistence (JPA) para mapeamento objeto-relacional
import jakarta.persistence.*;
// Importações do Lombok para geração de construtores, getters, setters e pattern Builder
import lombok.*;


// Indica que a classe é uma entidade JPA mapeada para uma tabela no banco de dados
@Entity
// Define o nome da tabela no banco de dados PostgreSQL
@Table(name = "books")
// Gera automaticamente os métodos getters para todos os campos
@Getter
// Gera automaticamente os métodos setters para todos os campos
@Setter
// Cria um construtor sem argumentos (exigido pelo JPA)
@NoArgsConstructor
// Cria um construtor com todos os argumentos
@AllArgsConstructor
// Habilita o padrão de projeto Builder para facilitar a criação de instâncias da entidade
@Builder
public class Book {

    // Define a chave primária da tabela
    @Id
    // Configura a geração automática do ID usando auto-incremento (SERIAL do PostgreSQL)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título do livro (campo obrigatório no banco)
    @Column(nullable = false)
    private String titulo;

    // Nome do autor do livro (campo obrigatório no banco)
    @Column(nullable = false)
    private String autor;

    // Código ISBN único do livro (campo obrigatório e sem duplicidade no banco)
    @Column(nullable = false, unique = true)
    private String isbn;

    // Quantidade total de exemplares adquiridos pela biblioteca
    @Column(nullable = false)
    private Integer totalExemplares;

    // Quantidade de exemplares atualmente disponíveis para empréstimo
    @Column(nullable = false)
    private Integer exemplaresDisponiveis;

}
