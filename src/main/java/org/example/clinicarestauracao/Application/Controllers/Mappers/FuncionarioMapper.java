package org.example.clinicarestauracao.Application.Controllers.Mappers;

import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioResponseDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioUpdateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.UserSummaryDto;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;

public final class FuncionarioMapper
{
    private FuncionarioMapper()
    {}

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

        return new FuncionarioResponseDto(entity.getId(), entity.getNome(), entity.getCpf(), entity.getEmail(), entity.getDataNascimento(), EnderecoMapper.entityToResponseDto(entity.getEndereco()), entity.getCep(), entity.isAtivo(), cargo, user, entity.getDataAdmissao(), entity.getDataDemissao());
    }

    public static Funcionario createRequestDtoToEntity(FuncionarioCreateRequestDto dto, Cargo cargo)
    {
        User user = dto.user() == null ? null : new User(dto.user().username(), dto.user().password(), UserRoles.USER);

        return new Funcionario(dto.nome(), dto.cpf(), dto.email(), dto.dataNascimento(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.cep(), user, cargo, dto.dataAdmissao());
    }

    public static Funcionario updateRequestDtoToEntity(FuncionarioUpdateRequestDto dto, Cargo cargo)
    {
        User user = dto.user() == null ? null : User.forCredentialsUpdate(dto.user().username(), dto.user().password());

        return new Funcionario(dto.nome(), dto.cpf(), dto.email(), dto.dataNascimento(), EnderecoMapper.requestDtoToEntity(dto.endereco()), dto.cep(), user, cargo, dto.dataAdmissao());
    }

}
