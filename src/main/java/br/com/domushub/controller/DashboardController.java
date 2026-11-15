package br.com.domushub.controller;

import br.com.domushub.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final ProdutoService service;
    public DashboardController(ProdutoService service) { this.service = service; }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalProdutos", service.contarProdutos());
        model.addAttribute("estoqueBaixo", service.estoqueBaixo());
        model.addAttribute("lotesVencendo", service.lotesVencendo());
        model.addAttribute("lotesVencidos", service.lotesVencidos());
        model.addAttribute("movimentacoes", service.ultimasMovimentacoes());
        return "dashboard";
    }
}
