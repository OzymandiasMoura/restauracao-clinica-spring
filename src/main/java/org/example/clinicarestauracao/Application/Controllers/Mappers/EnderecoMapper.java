package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

public class EnderecoMapper
{
    public static Endereco requestDtoToEntity(EnderecoDto dto)
    {
        if (dto == null)
        {
            return null;
        }

        return new Endereco(dto.logradouro(), dto.numero(), dto.bairro(), dto.cidade(), dto.estado(), dto.complemento());
    }

    public static EnderecoDto entityToResponseDto(Endereco entity)
    {
        if (entity == null)
        {
            return null;
        }

        return new EnderecoDto(entity.getLogradouro(), entity.getNumero(), entity.getBairro(), entity.getCidade(), entity.getEstado(), entity.getComplemento());
    }
}
