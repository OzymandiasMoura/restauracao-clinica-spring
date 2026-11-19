package org.example.clinicarestauracao.Application.Dtos.MovimentacaoEstoqueDtos;

import org.example.clinicarestauracao.Domain.Enums.TipoMovimentacao;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponseDto(
        Long id,
        Long medicamentoId,
        TipoMovimentacao tipo,
        Integer quantidade,
        LocalDateTime dataMovimentacao,
        String usuarioResponsavel,
        Integer saldoAnterior,
        Integer saldoPosterior,
        String justificativa
) {}