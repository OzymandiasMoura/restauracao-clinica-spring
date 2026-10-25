package org.example.clinicarestauracao.Application.Controllers.Mappers;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;

public final class TriagemMapper {

    private TriagemMapper() {
    }

    public static Triagem requestDtoToEntity(
            TriagemRequestDto dto,
            Convenio convenio
    ) {
        return new Triagem(
                convenio,
                dto.nomePossivelAcolhido(),
                dto.telefone(),
                dto.dataAgendada(),
                dto.observacaoEntrevista()
        );
    }

    public static TriagemResponseDto entityToResponseDto(Triagem entity) {
        return new TriagemResponseDto(
                entity.getId(),
                ConvenioMapper.entityToResponseDto(entity.getConvenio()),
                entity.getNomePossivelAcolhido(),
                entity.getTelefone(),
                entity.getDataAgendada(),
                entity.getDataConclusao(),
                entity.getObservacaoEntrevista(),
                entity.getStatus()
        );
    }
}