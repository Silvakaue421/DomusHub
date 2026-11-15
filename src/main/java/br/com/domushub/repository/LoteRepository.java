package br.com.domushub.repository;

import br.com.domushub.model.Lote;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByQuantidadeGreaterThanAndDataValidadeBetweenOrderByDataValidadeAsc(
        BigDecimal quantidade, LocalDate inicio, LocalDate fim);
    List<Lote> findByProdutoIdOrderByDataValidadeAscIdAsc(Long produtoId);
    List<Lote> findByProdutoIdOrderByIdAsc(Long produtoId);

    List<Lote> findByQuantidadeGreaterThanAndDataValidadeBeforeOrderByDataValidadeAsc(
        BigDecimal quantidade, LocalDate data);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Lote l where l.produto.id = :produtoId and l.quantidade > 0 and l.dataValidade >= :hoje order by l.dataValidade asc, l.id asc")
    List<Lote> buscarLotesDisponiveisParaSaida(@Param("produtoId") Long produtoId, @Param("hoje") LocalDate hoje);
}
