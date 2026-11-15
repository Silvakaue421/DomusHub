package br.com.domushub.repository;

import br.com.domushub.model.Perfil;
import br.com.domushub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByPerfilAndAtivoTrue(Perfil perfil);
    java.util.List<Usuario> findByAtivoTrueOrderByNomeAsc();
    java.util.List<Usuario> findByAtivoFalseOrderByNomeAsc();
}
