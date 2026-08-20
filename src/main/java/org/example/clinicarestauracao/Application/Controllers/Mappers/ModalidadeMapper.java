package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;

public final class ModalidadeMapper
{
    private ModalidadeMapper()
    {}

    public static Modalidade requestDtoToEntity(ModalidadeRequestDto dto)
    {
        return new Modalidade(dto.descricao(), dto.cnpj(), dto.maxVagas(), dto.pagamento());
    }

    public static ModalidadeResponseDto entityToResponseDto(Modalidade entity)
    {
        return new ModalidadeResponseDto(entity.getId(), entity.getDescricao(), entity.getCnpj(), entity.getMaxVagas(), entity.isAtivo(), entity.isPagamento());
    }
}
