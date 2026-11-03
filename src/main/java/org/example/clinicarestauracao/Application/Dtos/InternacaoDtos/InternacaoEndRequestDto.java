package org.example.clinicarestauracao.Application.Dtos.InternacaoDtos;

import org.example.clinicarestauracao.Domain.Enums.AltaTypes;

import java.time.LocalDate;

public record InternacaoEndRequestDto(AltaTypes alta, LocalDate dataSaida)
{
}
