package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoUpdateRequestDto;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;

public final class AcolhidoMapper
{
    private AcolhidoMapper() {}

    public static Acolhido createRequestDtoToEntity(AcolhidoCreateRequestDto dto, Responsavel responsavel)
    {
        return new Acolhido(dto.nome(), dto.email(), dto.cpf(), dto.moradorRua(), dto.cep(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.telefone(), dto.telefone2(), dto.dataNascimento(), dto.etnia(), dto.genero(), dto.orientacaoSexual(), responsavel, dto.grauParentesco());
    }
    public static Acolhido updateRequestDtoToEntity(AcolhidoUpdateRequestDto dto, Responsavel responsavel)
    {
        return new Acolhido(dto.nome(), dto.email(), dto.cpf(), dto.moradorRua(), dto.cep(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.telefone(), dto.telefone2(), dto.dataNascimento(), dto.etnia(), dto.genero(), dto.orientacaoSexual(), responsavel, dto.grauParentesco());
    }
    public static AcolhidoResponseDto entityToResponseDto(Acolhido entity)
    {
        return new AcolhidoResponseDto(entity.getId(), entity.getDataCriacao(), entity.getNome(), entity.getCpf(), entity.getEmail(), entity.getDataNascimento(), EnderecoMapper.entityToResponseDto(entity.getEndereco()), entity.getCep(), entity.getTelefone(), ResponsavelMapper.entityToResponseDto(entity.getResponsavel()), entity.getAtivo(), entity.getMoradorRua(), entity.getTelefone2(), entity.getEtnia(), entity.getGenero(), entity.getOrientacaoSexual(), entity.getGrauParentesco());
    }

}
