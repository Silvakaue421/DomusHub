package br.com.domushub.service;

import br.com.domushub.model.Perfil;
import br.com.domushub.model.Usuario;
import br.com.domushub.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;

    public UsuarioService(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional
    public ResultadoStatus inativar(Long id, String emailLogado) {
        Usuario usuario = usuarios.findById(id).orElse(null);
        if (usuario == null) return ResultadoStatus.NAO_ENCONTRADO;
        if (usuario.getEmail().equalsIgnoreCase(emailLogado)) return ResultadoStatus.PROPRIA_CONTA;
        if (!usuario.isAtivo()) return ResultadoStatus.JA_INATIVO;
        if (usuario.getPerfil() == Perfil.ADMIN
                && usuarios.countByPerfilAndAtivoTrue(Perfil.ADMIN) <= 1) {
            return ResultadoStatus.ULTIMO_ADMIN_ATIVO;
        }

        usuario.setAtivo(false);
        usuarios.save(usuario);
        return ResultadoStatus.SUCESSO;
    }

    @Transactional
    public ResultadoStatus reativar(Long id) {
        Usuario usuario = usuarios.findById(id).orElse(null);
        if (usuario == null) return ResultadoStatus.NAO_ENCONTRADO;
        if (usuario.isAtivo()) return ResultadoStatus.JA_ATIVO;

        usuario.setAtivo(true);
        usuarios.save(usuario);
        return ResultadoStatus.SUCESSO;
    }

    public enum ResultadoStatus {
        SUCESSO, NAO_ENCONTRADO, PROPRIA_CONTA, ULTIMO_ADMIN_ATIVO, JA_ATIVO, JA_INATIVO
    }
}
