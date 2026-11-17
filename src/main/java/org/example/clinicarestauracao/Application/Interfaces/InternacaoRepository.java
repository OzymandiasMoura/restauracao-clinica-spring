package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Application.Reports.InternacoesAtivasPorConvenio;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternacaoRepository extends JpaRepository<Internacao, Long>
{
    List<Internacao> findByAcolhido(Acolhido acolhido);
    List<Internacao> findByConvenio(Convenio convenio);
    boolean existsByAcolhidoAndAtivoTrue(Acolhido acolhido);
    boolean existsByAcolhido(Acolhido acolhido);
    boolean existsByConvenio(Convenio convenio);
    List<Internacao> findByAtivoTrue();
    long countByConvenioAndAtivoTrue(Convenio convenio);
    boolean existsByConvenioIdAndAtivoTrue(Long convenioId);
    @Query("""
        SELECT new org.example.clinicarestauracao.Application.Reports.InternacoesAtivasPorConvenio(
            c.id,
            c.descricao,
            COUNT(i.id)
        )
        FROM Convenio c
        LEFT JOIN Internacao i ON i.convenio = c AND i.ativo = true
        GROUP BY c.id, c.descricao
        ORDER BY c.descricao
        """)
    List<InternacoesAtivasPorConvenio> countInternacoesAtivasPorConvenio();

}
