package br.com.domushub.config;

import br.com.domushub.model.*;
import br.com.domushub.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner criarAdmin(UsuarioRepository usuarios, PasswordEncoder encoder) {
        return args -> {
            if (!usuarios.existsByEmail("admin@domushub.com")) {
                usuarios.save(new Usuario("Administrador", "admin@domushub.com",
                    encoder.encode("Domus@123"), Perfil.ADMIN));
            }
            if (!usuarios.existsByEmail("morador@domushub.com")) {
                usuarios.save(new Usuario("Morador de Teste", "morador@domushub.com",
                    encoder.encode("Morador@123"), Perfil.MORADOR));
            }
        };
    }
}
