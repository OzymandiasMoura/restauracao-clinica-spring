package org.example.clinicarestauracao.Application.Dtos.InternacaoDtos;

import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InternacaoResponseDto(Long id, AcolhidoResponseDto acolhido, ConvenioResponseDto convenio, LocalDate dataEntrada, LocalDate dataSaidaPrevista, LocalDate dataSaida, TreatmentStage etapaTratamento, BigDecimal valorMensalidade, boolean ativo, AltaTypes alta)
{
}
