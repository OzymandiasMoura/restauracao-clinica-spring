package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.MovimentacaoEstoqueDtos.MovimentacaoEstoqueResponseDto;
import org.example.clinicarestauracao.Domain.Entities.MovimentacaoEstoque;

public class MovimentacaoEstoqueMapper {

    private MovimentacaoEstoqueMapper() {
    }

    public static MovimentacaoEstoqueResponseDto entityToResponseDto(
            MovimentacaoEstoque movimentacao
    ) {
        return new MovimentacaoEstoqueResponseDto(
                movimentacao.getId(),
                movimentacao.getMedicamento().getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getUsuarioResponsavel(),
                movimentacao.getSaldoAnterior(),
                movimentacao.getSaldoPosterior(),
                movimentacao.getJustificativa()
        );
    }
}