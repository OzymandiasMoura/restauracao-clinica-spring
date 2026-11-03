package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;

import org.example.clinicarestauracao.Domain.Enums.UserRoles;

public record FuncionarioUserDataDto(String username, String password, UserRoles role)
{
}
