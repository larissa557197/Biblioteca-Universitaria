// Define o pacote onde a classe de segurança do JWT está localizada
package com.example.biblioteca_universitaria.security;

// Importações da biblioteca JJWT para manipulação e validação de tokens
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
// Importações do Spring Framework
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

// Marca a classe como um componente de Serviço (Service) gerenciado pelo Spring
@Service
public class JwtService {

    // Injeta o valor da chave secreta configurada no application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Injeta o tempo de expiração do token (em milissegundos) do application.properties
    @Value("${jwt.expiration}")
    private long expirationInMs;

    // Extrai o nome do usuário (Subject) contido dentro do payload do token JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Método genérico para extrair qualquer informação específica (Claim) do token
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Faz o parse do token usando a chave de assinatura para ler todos os dados (Claims) contidos nele
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Gera o objeto de Chave Criptográfica (Key) a partir do segredo em texto puro
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        // O algoritmo HS256 exige uma chave de no mínimo 256 bits (32 bytes).
        // Se a chave no application.properties for menor, preenche (padding) até atingir os 32 bytes de forma determinística
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            for (int i = 0; i < padded.length; i++) {
                padded[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = padded;
        }

        // Converte os bytes na chave secreta HMAC para o algoritmo HS256
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Cria e assina um novo token JWT para o usuário autenticado
    public String generateToken(UserDetails userDetails) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Define o identificador do usuário
                .setIssuedAt(agora)                    // Define a data/hora de criação
                .setExpiration(expiracao)              // Define a data/hora de expiração
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Assina digitalmente o token
                .compact();                           // Compila tudo em uma String formatada em JWT
    }

    // Valida se o token pertence ao usuário informado e se não está expirado
    public boolean isTokenValido(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    // Verifica se a data de expiração do token é anterior à data/hora atual
    private boolean isTokenExpirado(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}