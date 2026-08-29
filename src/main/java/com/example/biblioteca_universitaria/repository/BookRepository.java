package com.example.biblioteca_universitaria.repository;

import com.example.biblioteca_universitaria.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);
}
