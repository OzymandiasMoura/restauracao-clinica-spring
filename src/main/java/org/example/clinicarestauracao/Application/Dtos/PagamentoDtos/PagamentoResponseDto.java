package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponseDto(
        Long id,
        BigDecimal valor,
        LocalDate dataPagamento,
        Long internacaoId
) {}