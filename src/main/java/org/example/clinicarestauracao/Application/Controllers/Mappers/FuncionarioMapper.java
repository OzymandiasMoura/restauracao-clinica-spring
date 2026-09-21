package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioRequestDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioResponseDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.UserSummaryDto;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;

public final class FuncionarioMapper
{
    private FuncionarioMapper()
    {}

    public static Funcionario requestDtoToEntity(FuncionarioRequestDto dto, User user, Cargo cargo)
    {
        return new Funcionario(dto.nome(), dto.cpf(), dto.email(), dto.dataNascimento(), dto.endereco(), dto.cep(), user, cargo);
    }

    public static FuncionarioResponseDto entityToResponseDto(Funcionario entity)
    {
        CargoResponseDto cargo = CargoMapper.entityToResponseDto(entity.getCargo());
        UserSummaryDto user = entity.getUser() == null
                ? null
                : new UserSummaryDto(
                entity.getUser().getId(),
                entity.getUser().getUsername(),
                entity.getUser().getRole()
        );

        return new FuncionarioResponseDto(entity.getId(), entity.getNome(), entity.getCpf(), entity.getEmail(), entity.getDataNascimento(), entity.getEndereco(), entity.getCep(), entity.isAtivo(), cargo, user);
    }
}
