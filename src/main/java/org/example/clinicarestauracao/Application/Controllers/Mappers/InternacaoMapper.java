package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacoesAtivasByConvenioDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.TreatmentStageByConvenioDto;
import org.example.clinicarestauracao.Application.Reports.InternacoesAtivasByConvenio;
import org.example.clinicarestauracao.Application.Reports.TreatmentStageByConvenio;
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
            return new Internacao(acolhido, convenio, dto.dataEntrada());
        }

        return new Internacao(acolhido, convenio, dto.dataEntrada(), dto.valorMensalidade());
    }

    public static InternacaoResponseDto entityToResponseDto(Internacao entity)
    {
        return new InternacaoResponseDto(entity.getId(), AcolhidoMapper.entityToResponseDto(entity.getAcolhido()), ConvenioMapper.entityToResponseDto(entity.getConvenio()), entity.getDataEntrada(), entity.getDataSaidaPrevista(), entity.getDataSaida(), entity.getEtapaTratamento(), entity.getValorMensalidade(), entity.getAtivo(), entity.getAlta());
    }

    public static InternacoesAtivasByConvenioDto reportToResponseDto(InternacoesAtivasByConvenio report)
    {
        return new InternacoesAtivasByConvenioDto(report.convenioId(), report.descricao(), report.quantidadeAtivas());
    }


    public static TreatmentStageByConvenioDto reportToResponseDto(TreatmentStageByConvenio report
    )
    {
        return new TreatmentStageByConvenioDto(report.convenioId(), report.descricao(), report.quantidadeEmTratamento(), report.quantidadeProximoAlta(), report.quantidadeAltaVencida(), report.quantidadeAltaConcedida(), report.total());
    }
}
