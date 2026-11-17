package org.example.clinicarestauracao.Application.Dtos.InternacaoDtos;

public record TreatmentStageByConvenioDto( Long convenioId, String descricao, Long quantidadeEmTratamento, Long quantidadeProximoAlta, Long quantidadeAltaVencida, Long quantidadeAltaConcedida, Long total)
{
}
