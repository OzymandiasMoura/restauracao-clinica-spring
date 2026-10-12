package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.example.clinicarestauracao.Domain.Entities.Triagem;

public final class TriagemMapper {

    private TriagemMapper() {
    }

    public static Triagem requestDtoToEntity(
            TriagemRequestDto dto,
            Modalidade modalidade
    ) {
        return new Triagem(
                modalidade,
                dto.nomePossivelAcolhido(),
                dto.telefone(),
                dto.dataAgendada(),
                dto.observacaoEntrevista()
        );
    }

    public static TriagemResponseDto entityToResponseDto(Triagem entity) {
        return new TriagemResponseDto(
                entity.getId(),
                entity.getModalidade().getId(),
                entity.getModalidade().getDescricao(),
                entity.getNomePossivelAcolhido(),
                entity.getTelefone(),
                entity.getDataAgendada(),
                entity.getObservacaoEntrevista(),
                entity.getStatus()
        );
    }
}