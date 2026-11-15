package br.com.domushub.repository;

import br.com.domushub.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findTop20ByOrderByDataHoraDesc();
    boolean existsByUsuario_Id(Long usuarioId);
}
