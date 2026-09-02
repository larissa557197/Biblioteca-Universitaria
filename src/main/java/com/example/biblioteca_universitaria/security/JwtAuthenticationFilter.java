// Define o pacote onde a classe de segurança está localizadas
package com.example.biblioteca_universitaria.security;

// Importações de classes do Servlet (mecanismo básico de requisições web em Java)
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Importação do Spring e do Spring Security para manipular cabeçalhos e autenticação
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Marca a classe como um componente gerenciado pelo Spring (será injetado onde for necessário)
@Component
// Extende OncePerRequestFilter para garantir que este filtro execute apenas UMA vez por cada requisição HTTP
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Dependência do serviço responsável por gerar, extrair dados e validar os tokens JWT
    private final JwtService jwtService;
    // Dependência do serviço que busca os dados do usuário no banco de dados
    private final CustomUserDetailsService userDetailsService;

    // Construtor para injeção de dependência automática das duas classes acima
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Método principal que intercepta a requisição e faz a validação da autenticação
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Obtém o valor do cabeçalho "Authorization" que veio na requisição HTTP
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Verifica se o cabeçalho não existe ou se não começa com o prefixo padrão "Bearer "
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove o texto "Bearer " (os primeiros 7 caracteres) para extrair somente a string do token JWT
        String token = header.substring(7);
        // Extrai o nome de usuário (username/e-mail) armazenado dentro do token JWT
        String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValido(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
