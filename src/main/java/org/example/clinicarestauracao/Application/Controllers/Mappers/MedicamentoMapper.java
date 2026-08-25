package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;

public final class MedicamentoMapper {

    private MedicamentoMapper(){}
    public static Medicamento requestDtoToEntity(MedicamentoRequestDto dto)
    {
        return new Medicamento(dto.nome(), dto.preco());
    }

    public static MedicamentoResponseDto entityToResponseDto(Medicamento entity)
    {
        return new MedicamentoResponseDto(entity.getId(), entity.getNome(), entity.getPreco());
    }

}
