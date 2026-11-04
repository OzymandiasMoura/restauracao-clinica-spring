package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Pagamento.PagamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Pagamento.PagamentoWithInvalidInformationException;

import java.math.BigDecimal;
import java.time.LocalDate;

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


    public Pagamento(
            BigDecimal valor,
            LocalDate dataPagamento,
            Internacao internacao
    ) {
        setValor(valor);
        setDataPagamento(dataPagamento);
        setInternacao(internacao);
    }

    public void setValor(BigDecimal valor) {

        if (valor == null || valor.signum() <= 0) {
            throw new PagamentoWithInvalidInformationException(
                    "O valor do pagamento deve ser maior que zero."
            );
        }

        if (valor.scale() > 2 || valor.precision() - valor.scale() > 10) {
            throw new PagamentoWithInvalidInformationException(
                    "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais."
            );
        }

        this.valor = valor;
    }

    public void setDataPagamento(LocalDate dataPagamento) {

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

    public void setInternacao(Internacao internacao) {

        if (internacao == null) {
            throw new PagamentoWithInvalidInformationException(
                    "A internação é obrigatória."
            );
        }

        this.internacao = internacao;
    }
}
