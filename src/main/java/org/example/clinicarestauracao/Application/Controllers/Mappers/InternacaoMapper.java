package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;

public final class InternacaoMapper
{
    private InternacaoMapper()
    {
    }

    public static Internacao createRequestDtoToEntity(InternacaoCreateRequestDto dto, Acolhido acolhido, Convenio convenio)
    {
        if (dto.valorMensalidade() == null)
        {
            return new Internacao(acolhido, convenio, dto.dataEntrada(), dto.etapaTratamento());
        }

        return new Internacao(acolhido, convenio, dto.dataEntrada(), dto.etapaTratamento(), dto.valorMensalidade());
    }

    public static InternacaoResponseDto entityToResponseDto(Internacao entity)
    {
        return new InternacaoResponseDto(entity.getId(), AcolhidoMapper.entityToResponseDto(entity.getAcolhido()), ConvenioMapper.entityToResponseDto(entity.getConvenio()), entity.getDataEntrada(), entity.getDataSaidaPrevista(), entity.getDataSaida(), entity.getEtapaTratamento(), entity.getValorMensalidade(), entity.getAtivo(), entity.getAlta());
    }
}
