package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelResponseDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelUpdateRequestDto;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;

public final class ResponsavelMapper
{
    private ResponsavelMapper(){}

    public static Responsavel createRequestDtoToEntity(ResponsavelCreateRequestDto dto)
    {
        return new Responsavel(dto.nome(), dto.email(), dto.cpf(), dto.telefone(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.cep());
    }

    public static Responsavel updateRequestDtoToEntity(ResponsavelUpdateRequestDto dto)
    {
        return new Responsavel(dto.nome(), dto.email(), dto.cpf(), dto.telefone(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.cep());
    }

    public static ResponsavelResponseDto entityToResponseDto(Responsavel entity)
    {
        return new ResponsavelResponseDto(entity.getId(), entity.getNome(), entity.getCpf(), entity.getEmail(), EnderecoMapper.entityToResponseDto(entity.getEndereco()), entity.getCep(), entity.getTelefone(), entity.isAtivo());
    }


}
