package org.example.clinicarestauracao.Application.Dtos.InternacaoDtos;

import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InternacaoUpdateRequestDto(LocalDate dataSaidaPrevista, TreatmentStage etapaTratamento, BigDecimal valorMensalidade)
{
}
