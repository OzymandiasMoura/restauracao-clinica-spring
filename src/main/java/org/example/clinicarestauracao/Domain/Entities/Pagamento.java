
package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Pagamento.PagamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.PagamentoStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "valor",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valor;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_internacao", nullable = false)
    private Internacao internacao;

    @Column(name = "competencia", nullable = false, length = 7)
    private String competencia;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PagamentoStatus status;

    @Column(name = "data_estorno")
    private LocalDateTime dataEstorno;

    @Column(name = "motivo_estorno", length = 500)
    private String motivoEstorno;

    public Pagamento(
            BigDecimal valor,
            LocalDate dataPagamento,
            Internacao internacao,
            String competencia,
            String observacao
    ) {
        setValor(valor);
        setDataPagamento(dataPagamento);
        setInternacao(internacao);
        setCompetencia(competencia);
        setObservacao(observacao);

        this.status = PagamentoStatus.ATIVO;
        this.dataEstorno = null;
        this.motivoEstorno = null;
    }

    private void setValor(BigDecimal valor) {

        if (valor == null || valor.signum() <= 0) {
            throw new PagamentoWithInvalidInformationException(
                    "O valor do pagamento deve ser maior que zero."
            );
        }

        if (valor.scale() > 2
                || valor.precision() - valor.scale() > 10) {
            throw new PagamentoWithInvalidInformationException(
                    "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais."
            );
        }

        this.valor = valor;
    }

    private void setDataPagamento(LocalDate dataPagamento) {

        if (dataPagamento == null) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento é obrigatória."
            );
        }

        if (dataPagamento.isAfter(LocalDate.now())) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento não pode ser futura."
            );
        }

        this.dataPagamento = dataPagamento;
    }

    private void setInternacao(Internacao internacao) {

        if (internacao == null) {
            throw new PagamentoWithInvalidInformationException(
                    "A internação é obrigatória."
            );
        }

        this.internacao = internacao;
    }

    private void setCompetencia(String competencia) {

        if (competencia == null || competencia.isBlank()) {
            throw new PagamentoWithInvalidInformationException(
                    "A competência é obrigatória."
            );
        }

        String competenciaFormatada = competencia.trim();

        try {
            YearMonth.parse(competenciaFormatada);
        } catch (DateTimeParseException ex) {
            throw new PagamentoWithInvalidInformationException(
                    "A competência deve estar no formato AAAA-MM."
            );
        }

        this.competencia = competenciaFormatada;
    }

    private void setObservacao(String observacao) {

        if (observacao == null || observacao.isBlank()) {
            this.observacao = null;
            return;
        }

        String observacaoFormatada = observacao.trim();

        if (observacaoFormatada.length() > 500) {
            throw new PagamentoWithInvalidInformationException(
                    "A observação não pode ultrapassar 500 caracteres."
            );
        }

        this.observacao = observacaoFormatada;
    }

    public void estornar(String motivo) {

        if (this.status == PagamentoStatus.ESTORNADO) {
            throw new PagamentoWithInvalidInformationException(
                    "O pagamento já está estornado."
            );
        }

        if (motivo == null || motivo.isBlank()) {
            throw new PagamentoWithInvalidInformationException(
                    "O motivo do estorno é obrigatório."
            );
        }

        String motivoFormatado = motivo.trim();

        if (motivoFormatado.length() > 500) {
            throw new PagamentoWithInvalidInformationException(
                    "O motivo do estorno não pode ultrapassar 500 caracteres."
            );
        }

        this.status = PagamentoStatus.ESTORNADO;
        this.dataEstorno = LocalDateTime.now();
        this.motivoEstorno = motivoFormatado;
    }
}
