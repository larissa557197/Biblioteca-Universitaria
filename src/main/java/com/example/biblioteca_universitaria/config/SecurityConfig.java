// Define o pacote onde a classe de configuração está localizada
package com.example.biblioteca_universitaria.config;

// Importação do filtro customizado que intercepta e valida os tokens JWT em cada requisição
import com.example.biblioteca_universitaria.security.JwtAuthenticationFilter;
// Anotações do Spring para declarar métodos gerenciados (Beans) e classes de configuração
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
// Classes do Spring Security para gerenciamento e autenticação de usuários
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// Classes para criptografia de senhas (hash)
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Interface da cadeia de filtros de segurança e do filtro padrão de usuário/senha
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Indica ao Spring que esta classe contém configurações de Beans da aplicação
@Configuration
// Habilita a proteção de segurança a nível de métodos (ex: @PreAuthorize nas rotas)
@EnableMethodSecurity
public class SecurityConfig {

    // Declaração da dependência do filtro JWT que interceptará os requests
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Construtor para injeção de dependência do filtro JWT feita automaticamente pelo Spring
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Registra o método como um Bean que define a cadeia principal de segurança da aplicação
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desativa a proteção CSRF, desnecessária em APIs REST que usam autenticação via Token (Stateless)
                .csrf(csrf -> csrf.disable())
                // Configura a política de criação de sessão como STATELESS (a API não salva estado nem cookies no servidor)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Define as regras de autorização para cada padrão de URL/endpoint
                .authorizeHttpRequests(auth -> auth
                        // Libera o acesso sem autenticação (permitAll) para as rotas listadas dentro do requestMatchers
                        .requestMatchers(
                                "/api/auth/**", // Endpoints de cadastro e login
                                "/v3/api-docs/**", // Dados JSON da documentação da API (OpenAPI)
                                "/swagger-ui/**", // Interface gráfica do Swagger
                                "/swagger-ui.html" // Página principal de acesso ao Swagger UI
                        ).permitAll()

                        // Permite leitura de livros para alunos e administradores
                        .requestMatchers(HttpMethod.GET, "/api/livros/**").hasAnyRole("STUDENT", "ADMIN")

                        // Restringe criação, edição e exclusão de livros exclusivamente para administradores
                        .requestMatchers(HttpMethod.POST, "/api/livros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/livros/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/livros/**").hasRole("ADMIN")

                        // Exige que qualquer outra requisição (anyRequest) esteja devidamente autenticada
                        .anyRequest().authenticated()
                )
                // Adiciona o filtro customizado do JWT para rodar ANTES do filtro padrão de autenticação do Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Constrói e retorna a configuração completa do filtro de segurança
        return http.build();
    }

    // Registra o algoritmo BCrypt como o codificador padrão para salvar e comparar senhas com hash seguro
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expõe o AuthenticationManager do Spring como um Bean para ser usado no Controller durante o Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }
}