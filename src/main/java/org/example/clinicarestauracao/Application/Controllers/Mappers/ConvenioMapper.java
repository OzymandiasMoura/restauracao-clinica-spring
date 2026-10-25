package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Convenio;

public final class ConvenioMapper
{
    private ConvenioMapper()
    {}

    public static Convenio requestDtoToEntity(ConvenioRequestDto dto)
    {
        return new Convenio(dto.descricao(), dto.cnpj(), dto.maxVagas(), dto.pagamento(), dto.cor());
    }

    public static ConvenioResponseDto entityToResponseDto(Convenio entity)
    {
        return new ConvenioResponseDto(entity.getId(), entity.getDescricao(), entity.getCnpj(), entity.getMaxVagas(), entity.isAtivo(), entity.isPagamento(),  entity.getCor());
    }
}
