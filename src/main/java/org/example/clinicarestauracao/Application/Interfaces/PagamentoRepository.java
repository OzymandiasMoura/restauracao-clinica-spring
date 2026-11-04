package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @Query("""
    SELECT COALESCE(SUM(p.valor), 0)
    FROM Pagamento p
    WHERE p.internacao.id = :internacaoId
    """)
    BigDecimal calcularTotalPago(
            @Param("internacaoId") Long internacaoId
    );

    List<Pagamento> findByInternacaoId(Long internacaoId);
}
