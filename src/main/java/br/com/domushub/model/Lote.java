package br.com.domushub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lotes", uniqueConstraints = @UniqueConstraint(columnNames = {"produto_id", "numero_no_produto"}))
public class Lote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_no_produto")
    private Integer numeroNoProduto;

    @ManyToOne(optional = false) @JoinColumn(name = "produto_id")
    private Produto produto;

    @NotNull @DecimalMin("0.0")
    private BigDecimal quantidade;

    @NotNull
    private LocalDate dataValidade;

    public Lote() {}
    public Lote(Produto produto, BigDecimal quantidade, LocalDate dataValidade) {
        this.produto = produto; this.quantidade = quantidade; this.dataValidade = dataValidade;
    }
    public Long getId() { return id; }
    public Integer getNumeroNoProduto() { return numeroNoProduto; }
    public void setNumeroNoProduto(Integer numeroNoProduto) { this.numeroNoProduto = numeroNoProduto; }
    public String getCodigoVisual() {
        if (numeroNoProduto == null) return "LOTE-PENDENTE";
        String base = java.text.Normalizer.normalize(produto.getNome(), java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "").toUpperCase(java.util.Locale.ROOT)
            .replaceAll("[^A-Z0-9]+", "-").replaceAll("^-|-$", "");
        if (base.isBlank()) base = "PRODUTO";
        if (base.length() > 12) base = base.substring(0, 12).replaceAll("-$", "");
        return base + "-" + String.format(java.util.Locale.ROOT, "%03d", numeroNoProduto);
    }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
}
