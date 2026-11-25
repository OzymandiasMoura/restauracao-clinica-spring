package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Application.Reports.InternacaoMonthlyCountProjection;
import org.example.clinicarestauracao.Application.Reports.InternacoesAtivasByConvenio;
import org.example.clinicarestauracao.Application.Reports.TreatmentStageByConvenio;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    Page<Internacao> findInternacaoByAcolhido_CpfContainingIgnoreCase(String cpf, Pageable pageable);

    Page<Internacao> findInternacaoByEtapaTratamento(TreatmentStage etapaTratamento, Pageable pageable);

    Page<Internacao> findInternacaoByDataEntradaBetween(LocalDate inicio, LocalDate fim, Pageable pageable);

    boolean existsByAcolhidoAndAlta(Acolhido acolhido, AltaTypes alta);

    Long countByAtivoTrue();

    long countByEtapaTratamentoAndAtivoTrue(TreatmentStage etapaTratamento);

    @Query(value = """
    SELECT
        CAST(EXTRACT(YEAR FROM data_entrada) AS INTEGER) AS ano,
        CAST(EXTRACT(MONTH FROM data_entrada) AS INTEGER) AS mes,
        COUNT(*) AS qtde
    FROM internacao
    WHERE data_entrada BETWEEN :inicio AND :fim
    GROUP BY
        CAST(EXTRACT(YEAR FROM data_entrada) AS INTEGER),
        CAST(EXTRACT(MONTH FROM data_entrada) AS INTEGER)
    ORDER BY ano, mes
    """, nativeQuery = true)
    List<InternacaoMonthlyCountProjection> countEntradasGroupByMonth(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query(value = """
    SELECT
        CAST(EXTRACT(YEAR FROM data_saida) AS INTEGER) AS ano,
        CAST(EXTRACT(MONTH FROM data_saida) AS INTEGER) AS mes,
        COUNT(*) AS qtde
    FROM internacao
    WHERE data_saida BETWEEN :inicio AND :fim
    GROUP BY
        CAST(EXTRACT(YEAR FROM data_saida) AS INTEGER),
        CAST(EXTRACT(MONTH FROM data_saida) AS INTEGER)
    ORDER BY ano, mes
    """, nativeQuery = true)
    List<InternacaoMonthlyCountProjection> countSaidasGroupByMonth(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
