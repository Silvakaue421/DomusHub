package br.com.domushub.controller;

import br.com.domushub.model.*;
import br.com.domushub.repository.UsuarioRepository;
import br.com.domushub.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository usuarios, PasswordEncoder encoder, UsuarioService usuarioService) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, Authentication authentication) {
        model.addAttribute("usuariosAtivos", usuarios.findByAtivoTrueOrderByNomeAsc());
        model.addAttribute("usuariosInativos", usuarios.findByAtivoFalseOrderByNomeAsc());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("emailLogado", authentication.getName());
        return "usuarios";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors() || usuarios.existsByEmail(usuario.getEmail())) {
            ra.addFlashAttribute("erro", "Confira os campos. O e-mail pode já estar cadastrado.");
            return "redirect:/usuarios";
        }
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        usuarios.save(usuario);
        ra.addFlashAttribute("sucesso", "Usuário cadastrado.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/inativar")
    public String inativar(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {
        UsuarioService.ResultadoStatus resultado = usuarioService.inativar(id, authentication.getName());
        switch (resultado) {
            case SUCESSO -> ra.addFlashAttribute("sucesso", "Usuário inativado. O histórico foi preservado.");
            case NAO_ENCONTRADO -> ra.addFlashAttribute("erro", "Usuário não encontrado.");
            case PROPRIA_CONTA -> ra.addFlashAttribute("erro", "Você não pode inativar a própria conta.");
            case ULTIMO_ADMIN_ATIVO -> ra.addFlashAttribute("erro", "Não é possível inativar o último administrador ativo.");
            case JA_INATIVO -> ra.addFlashAttribute("erro", "Este usuário já está inativo.");
            case JA_ATIVO -> ra.addFlashAttribute("erro", "Este usuário já está ativo.");
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/reativar")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        UsuarioService.ResultadoStatus resultado = usuarioService.reativar(id);
        switch (resultado) {
            case SUCESSO -> ra.addFlashAttribute("sucesso", "Usuário reativado. O acesso foi liberado novamente.");
            case NAO_ENCONTRADO -> ra.addFlashAttribute("erro", "Usuário não encontrado.");
            case JA_ATIVO -> ra.addFlashAttribute("erro", "Este usuário já está ativo.");
            case JA_INATIVO -> ra.addFlashAttribute("erro", "Este usuário já está inativo.");
            case PROPRIA_CONTA -> ra.addFlashAttribute("erro", "Não é possível realizar esta ação.");
            case ULTIMO_ADMIN_ATIVO -> ra.addFlashAttribute("erro", "Não é possível realizar esta ação.");
        }
        return "redirect:/usuarios";
    }

}
