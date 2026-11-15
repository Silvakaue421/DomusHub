package br.com.domushub.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Produto produto;
    @ManyToOne(optional = false) private Usuario usuario;
    @Enumerated(EnumType.STRING) private TipoMovimentacao tipo;
    private BigDecimal quantidade;
    private LocalDateTime dataHora = LocalDateTime.now();

    public Movimentacao() {}
    public Movimentacao(Produto produto, Usuario usuario, TipoMovimentacao tipo, BigDecimal quantidade) {
        this.produto = produto; this.usuario = usuario; this.tipo = tipo; this.quantidade = quantidade;
    }
    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public Usuario getUsuario() { return usuario; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
}
