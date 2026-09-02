// Define o pacote onde o Controller de Autenticação está localizado
package com.example.biblioteca_universitaria.controller;

// Importações dos modelos, DTOs e Repositórios do seu projeto
import com.example.biblioteca_universitaria.domain.User;
import com.example.biblioteca_universitaria.domain.enums.Role;
import com.example.biblioteca_universitaria.dto.AuthResponse;
import com.example.biblioteca_universitaria.dto.LoginRequest;
import com.example.biblioteca_universitaria.dto.RegisterRequest;
import com.example.biblioteca_universitaria.repository.UserRepository;
import com.example.biblioteca_universitaria.security.CustomUserDetails;
import com.example.biblioteca_universitaria.security.JwtService;
// Importações do Spring MVC e Spring Security
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Define a classe como um Controller REST (retorna dados em JSON)
@RestController
// Define a rota base da API para os endpoints deste controller (/api/auth)
@RequestMapping("/api/auth")
public class AuthController {

    // Declaração dos serviços e repositórios necessários via injeção de dependência
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Construtor para injeção automática de dependências pelo Spring
    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Endpoint HTTP POST em /api/auth/register para cadastrar novos usuários
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Valida se o e-mail informado já existe no banco de dados
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado");
        }

        // Mapeia a permissão (Role) com base na String enviada na requisição
        Role role;
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            role = Role.ROLE_ADMIN;
        } else {
            role = Role.ROLE_STUDENT;
        }

        // Cria o objeto User usando o padrão Builder e criptografa a senha antes de salvar
        User usuario = User.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(role)
                .build();

        // Salva a entidade de usuário no banco de dados via JPA
        userRepository.save(usuario);

        // Retorna HTTP 200 OK com mensagem de sucesso
        return ResponseEntity.ok("Usuário registrado com sucesso");
    }

    // Endpoint HTTP POST em /api/auth/login para autenticar e gerar o token JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // Cria um token não autenticado com as credenciais enviadas (e-mail e senha)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                );

        // O Spring Security valida as credenciais contra o banco (lança exceção se for inválido)
        authenticationManager.authenticate(authToken);

        // Busca o usuário confirmado no banco de dados
        User usuario = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Empacota o usuário no adaptador CustomUserDetails
        CustomUserDetails userDetails = new CustomUserDetails(usuario);

        // Gera o token JWT para o usuário autenticado
        String token = jwtService.generateToken(userDetails);

        // Retorna HTTP 200 OK contendo o token no corpo da resposta
        return ResponseEntity.ok(new AuthResponse(token));
    }
}