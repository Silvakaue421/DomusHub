package br.com.domushub.config;

import br.com.domushub.service.UsuarioDetailsService;
import br.com.domushub.repository.UsuarioRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UsuarioDetailsService service, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider provider, UsuarioRepository usuarios) throws Exception {
        http.authenticationProvider(provider)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()
                // Apenas administradores podem cadastrar, editar ou excluir produtos.
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/produtos/novo", "/produtos/*/editar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/produtos/salvar", "/produtos/*/excluir").hasRole("ADMIN")
                // ADMIN e MORADOR podem registrar entradas e saídas.
                .anyRequest().authenticated())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .addFilterAfter(new UsuarioAtivoFilter(usuarios), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
