package br.com.domushub.controller;

import br.com.domushub.model.*;
import br.com.domushub.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService service;
    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required=false) String busca, Model model) {
        model.addAttribute("produtos", service.listar(busca));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "produtos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscar(id));
        return "produto-form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Produto produto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "produto-form";
        try {
            if (produto.getId() != null) {
                Produto existente = service.buscar(produto.getId());
                existente.setNome(produto.getNome());
                existente.setCategoria(produto.getCategoria());
                existente.setUnidade(produto.getUnidade());
                existente.setEstoqueMinimo(produto.getEstoqueMinimo());
                service.salvar(existente);
            } else {
                service.salvar(produto);
            }
            ra.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
            return "redirect:/produtos";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/produtos";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        service.excluir(id);
        ra.addFlashAttribute("sucesso", "Produto excluído.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/movimentar")
    public String movimentar(@PathVariable Long id, @RequestParam TipoMovimentacao tipo,
            @RequestParam BigDecimal quantidade, @RequestParam(required=false) String validadeTexto,
            Authentication auth, RedirectAttributes ra) {
        try {
            LocalDate validade = tipo == TipoMovimentacao.ENTRADA ?
                (validadeTexto == null || validadeTexto.isBlank() ? null : LocalDate.parse(validadeTexto)) : null;
            service.movimentar(id, auth.getName(), tipo, quantidade, validade);
            ra.addFlashAttribute("sucesso", "Estoque atualizado.");
        } catch (IllegalArgumentException | java.time.DateTimeException e) {
            ra.addFlashAttribute("erro", e.getMessage() == null ? "Data de validade inválida." : e.getMessage());
        }
        return "redirect:/produtos";
    }

    @GetMapping("/{id}/detalhes")
    public String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscar(id));
        model.addAttribute("lotes", service.listarLotes(id));
        return "produto-detalhes";
    }
}
