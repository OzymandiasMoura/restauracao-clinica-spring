package org.example.clinicarestauracao.Application.Dtos.Triagem;

import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Domain.Enums.TriagemStatus;

import java.time.LocalDateTime;

public record TriagemResponseDto(
        Long id,
        ConvenioResponseDto convenio,
        String nomePossivelAcolhido,
        String telefone,
        LocalDateTime dataAgendada,
        LocalDateTime dataConclusao,
        String observacaoEntrevista,
        TriagemStatus triagemStatus,
        LocalDateTime dataCancelamento,
        String motivoCancelamento
) {

}