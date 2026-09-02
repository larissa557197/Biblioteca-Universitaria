// Define o pacote onde a classe de serviço de segurança está localizada
package com.example.biblioteca_universitaria.security;

// Importações do modelo de domínio (User) e do repositório (UserRepository)
import com.example.biblioteca_universitaria.domain.User;
import com.example.biblioteca_universitaria.repository.UserRepository;
// Importações das interfaces do Spring Security para gerenciamento de detalhes de usuários
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Indica ao Spring que esta classe é um serviço (Service) gerenciado pelo container de injeção de dependências
@Service
// Implementa a interface nativa UserDetailsService do Spring Security
public class CustomUserDetailsService implements UserDetailsService {

    // Injeção da interface de repositório para acesso aos dados da tabela de usuários
    private final UserRepository userRepository;

    // Construtor para injeção de dependência feita automaticamente pelo Spring
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Método obrigatório da interface UserDetailsService chamado pelo Spring durante a autenticação
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo e-mail; se não encontrar, lança a exceção padrão do Spring Security
        User usuario = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Empacota a entidade User encontrada dentro do adapter CustomUserDetails exigido pelo Spring Security
        return new CustomUserDetails(usuario);
    }
}