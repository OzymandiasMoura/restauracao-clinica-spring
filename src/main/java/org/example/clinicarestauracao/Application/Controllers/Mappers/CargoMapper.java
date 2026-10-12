package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Domain.Entities.Cargo;

public final class CargoMapper
{
    private CargoMapper(){}

    public static Cargo requestDtoToEntity(CargoRequestDto dto)
    {
        return new Cargo(dto.nome());
    }

    public static CargoResponseDto entityToResponseDto(Cargo entity)
    {
        return new CargoResponseDto(entity.getId(), entity.getNome(), entity.isAtivo());
    }
}
