package org.example.clinicarestauracao.Application.Dtos.Triagem;

import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeResponseDto;
import org.example.clinicarestauracao.Domain.Enums.Status;

import java.time.LocalDateTime;

public record TriagemResponseDto(
        Long id,
        ModalidadeResponseDto modalidade,
        String nomePossivelAcolhido,
        String telefone,
        LocalDateTime dataAgendada,
        String observacaoEntrevista,
        Status status
) {

}