package org.example.clinicarestauracao.Application.Dtos.TriagemDtos;

import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Domain.Enums.Status;

import java.time.LocalDateTime;

public record TriagemResponseDto(
        Long id,
        ConvenioResponseDto convenio,
        String nomePossivelAcolhido,
        String telefone,
        LocalDateTime dataAgendada,
        LocalDateTime dataConclusao,
        String observacaoEntrevista,
        Status status
) {

}