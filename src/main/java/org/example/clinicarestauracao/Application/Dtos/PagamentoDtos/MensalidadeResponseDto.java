package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import org.example.clinicarestauracao.Domain.Enums.MensalidadeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MensalidadeResponseDto(
        String competencia,
        LocalDate dataVencimento,
        BigDecimal valor,
        MensalidadeStatus status
) {}