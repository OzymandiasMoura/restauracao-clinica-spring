package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import java.math.BigDecimal;
import java.util.List;

public record ResumoMensalidadesResponseDto(
        Long internacaoId,
        BigDecimal valorMensalidade,
        int quantidadeMensalidades,
        long mensalidadesPagas,
        long mensalidadesPendentes,
        long mensalidadesAtrasadas,
        BigDecimal totalDevido,
        BigDecimal totalPago,
        BigDecimal totalPendente,
        BigDecimal totalAtrasado,
        List<MensalidadeResponseDto> mensalidades
) {}