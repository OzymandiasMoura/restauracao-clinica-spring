package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import java.math.BigDecimal;

public record PagamentoResumoResponseDto(
        Long internacaoId,
        BigDecimal totalPago
) {}