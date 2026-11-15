package br.com.domushub.service;

import br.com.domushub.repository.UsuarioRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;
    public UsuarioDetailsService(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var usuario = usuarios.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        if (!usuario.isAtivo()) {
            throw new DisabledException("Esta conta está inativa. Procure o administrador do sistema.");
        }
        return User.withUsername(usuario.getEmail())
            .password(usuario.getSenha())
            .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()))
            .build();
    }
}
