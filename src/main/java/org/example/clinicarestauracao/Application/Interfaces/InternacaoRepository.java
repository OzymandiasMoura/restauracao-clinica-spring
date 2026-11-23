package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Application.Reports.InternacoesAtivasByConvenio;
import org.example.clinicarestauracao.Application.Reports.TreatmentStageByConvenio;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
        SELECT new org.example.clinicarestauracao.Application.Reports.InternacoesAtivasByConvenio(
            c.id,
            c.descricao,
            COUNT(i.id)
        )
        FROM Convenio c
        LEFT JOIN Internacao i ON i.convenio = c AND i.ativo = true
        GROUP BY c.id, c.descricao
        ORDER BY c.descricao
        """)
    List<InternacoesAtivasByConvenio> countInternacoesAtivasPorConvenio();

    @Query("""
        SELECT new org.example.clinicarestauracao.Application.Reports.TreatmentStageByConvenio(
            c.id,
            c.descricao,
            SUM(CASE WHEN i.etapaTratamento = org.example.clinicarestauracao.Domain.Enums.TreatmentStage.EM_TRATAMENTO
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.etapaTratamento = org.example.clinicarestauracao.Domain.Enums.TreatmentStage.PROXIMO_ALTA
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.etapaTratamento = org.example.clinicarestauracao.Domain.Enums.TreatmentStage.ALTA_VENCIDA
                THEN 1 ELSE 0 END),
            SUM(CASE WHEN i.etapaTratamento = org.example.clinicarestauracao.Domain.Enums.TreatmentStage.ALTA_CONCEDIDA
                THEN 1 ELSE 0 END),
            COUNT(i.id)
        )
        FROM Convenio c
        LEFT JOIN Internacao i ON i.convenio = c
        GROUP BY c.id, c.descricao
        ORDER BY c.descricao
        """)
    List<TreatmentStageByConvenio> findTreatmentStageByConvenio();

    Page<Internacao> findInternacaoByAcolhido_NomeContainingIgnoreCase(String nome, Pageable pageable);

}
