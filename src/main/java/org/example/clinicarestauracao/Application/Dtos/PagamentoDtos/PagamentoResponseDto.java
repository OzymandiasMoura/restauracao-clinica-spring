package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import org.example.clinicarestauracao.Domain.Enums.PagamentoStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagamentoResponseDto(
        Long id,
        BigDecimal valor,
        LocalDate dataPagamento,
        Long internacaoId,
        String competencia,
        String observacao,
        PagamentoStatus status,
        LocalDateTime dataEstorno,
        String motivoEstorno
) {}