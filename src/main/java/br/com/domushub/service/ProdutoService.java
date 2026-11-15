package br.com.domushub.service;

import br.com.domushub.model.*;
import br.com.domushub.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {
    private final ProdutoRepository produtos;
    private final LoteRepository lotes;
    private final MovimentacaoRepository movimentacoes;
    private final UsuarioRepository usuarios;

    public ProdutoService(ProdutoRepository produtos, LoteRepository lotes,
            MovimentacaoRepository movimentacoes, UsuarioRepository usuarios) {
        this.produtos = produtos; this.lotes = lotes;
        this.movimentacoes = movimentacoes; this.usuarios = usuarios;
    }

    public List<Produto> listar(String busca) {
        return (busca == null || busca.isBlank()) ? produtos.findAll() : produtos.findByNomeContainingIgnoreCase(busca);
    }
    public Produto buscar(Long id) {
        return produtos.findById(id).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }
    public Produto salvar(Produto produto) {
        if (produto.getEstoqueMinimo() == null || produto.getEstoqueMinimo().signum() < 0)
            throw new IllegalArgumentException("O estoque mínimo não pode ser negativo.");
        if (produto.getId() == null) produto.setQuantidade(BigDecimal.ZERO);
        return produtos.save(produto);
    }

    @Transactional
    public List<Lote> listarLotes(Long produtoId) {
        buscar(produtoId);
        garantirCodigosLotes(produtoId);
        return lotes.findByProdutoIdOrderByDataValidadeAscIdAsc(produtoId);
    }

    private int garantirCodigosLotes(Long produtoId) {
        List<Lote> todos = lotes.findByProdutoIdOrderByIdAsc(produtoId);
        int proximo = 1;
        boolean alterado = false;
        for (Lote lote : todos) {
            if (lote.getNumeroNoProduto() == null) {
                lote.setNumeroNoProduto(proximo);
                alterado = true;
            }
            proximo = Math.max(proximo, lote.getNumeroNoProduto() + 1);
        }
        if (alterado) lotes.saveAll(todos);
        return proximo;
    }
    public void excluir(Long id) { produtos.delete(buscar(id)); }

    @Transactional
    public void movimentar(Long produtoId, String email, TipoMovimentacao tipo,
            BigDecimal quantidade, LocalDate validade) {
        if (quantidade == null || quantidade.signum() <= 0)
            throw new IllegalArgumentException("Informe uma quantidade maior que zero.");
        if (tipo == null)
            throw new IllegalArgumentException("Selecione entrada ou saída.");
        Produto produto = produtos.buscarPorIdComBloqueio(produtoId)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        int proximoNumeroLote = garantirCodigosLotes(produtoId);
        Usuario usuario = usuarios.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (tipo == TipoMovimentacao.ENTRADA) {
            if (validade == null)
                throw new IllegalArgumentException("Informe a validade para registrar a entrada.");
            if (validade.isBefore(LocalDate.now()))
                throw new IllegalArgumentException("A validade não pode estar no passado.");
            Lote novoLote = new Lote(produto, quantidade, validade);
            novoLote.setNumeroNoProduto(proximoNumeroLote);
            lotes.save(novoLote);
        } else {
            List<Lote> disponiveis = lotes.buscarLotesDisponiveisParaSaida(produtoId, LocalDate.now());
            BigDecimal disponivel = disponiveis.stream()
                .map(Lote::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (disponivel.compareTo(quantidade) < 0)
                throw new IllegalArgumentException("Estoque válido insuficiente. Disponível: " + disponivel + ".");

            BigDecimal restante = quantidade;
            for (Lote lote : disponiveis) {
                if (restante.signum() == 0) break;
                BigDecimal retirar = lote.getQuantidade().min(restante);
                lote.setQuantidade(lote.getQuantidade().subtract(retirar));
                restante = restante.subtract(retirar);
            }
            lotes.saveAll(disponiveis);
        }

        BigDecimal total = lotes.findByProdutoIdOrderByDataValidadeAscIdAsc(produtoId).stream()
            .map(Lote::getQuantidade).reduce(BigDecimal.ZERO, BigDecimal::add);
        produto.setQuantidade(total);
        produtos.save(produto);
        movimentacoes.save(new Movimentacao(produto, usuario, tipo, quantidade));
    }

    @Transactional
    public List<Lote> lotesVencidos() {
        List<Lote> vencidos = lotes.findByQuantidadeGreaterThanAndDataValidadeBeforeOrderByDataValidadeAsc(
            BigDecimal.ZERO, LocalDate.now());
        vencidos.stream().map(l -> l.getProduto().getId()).distinct()
            .forEach(this::garantirCodigosLotes);
        return vencidos;
    }
    public List<Lote> lotesVencendo() {
        return lotes.findByQuantidadeGreaterThanAndDataValidadeBetweenOrderByDataValidadeAsc(
            BigDecimal.ZERO, LocalDate.now(), LocalDate.now().plusDays(7));
    }
    public List<Produto> estoqueBaixo() {
        return produtos.findAll().stream()
            .filter(p -> p.getQuantidade().compareTo(p.getEstoqueMinimo()) <= 0).toList();
    }
    public long contarProdutos() { return produtos.count(); }
    public List<Movimentacao> ultimasMovimentacoes() { return movimentacoes.findTop20ByOrderByDataHoraDesc(); }
}
