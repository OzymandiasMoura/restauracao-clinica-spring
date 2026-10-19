package org.example.clinicarestauracao.Application.Dtos.InternacaoDtos;

import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InternacaoCreateRequestDto(Long acolhidoId, Long convenioId, LocalDate dataEntrada, TreatmentStage etapaTratamento, BigDecimal valorMensalidade)
{
}
