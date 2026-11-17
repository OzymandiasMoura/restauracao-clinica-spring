package org.example.clinicarestauracao.Application.Reports;

public record TreatmentStageByConvenio(Long convenioId, String descricao, Long quantidadeEmTratamento, Long quantidadeProximoAlta, Long quantidadeAltaVencida, Long quantidadeAltaConcedida, Long total)
{
}
