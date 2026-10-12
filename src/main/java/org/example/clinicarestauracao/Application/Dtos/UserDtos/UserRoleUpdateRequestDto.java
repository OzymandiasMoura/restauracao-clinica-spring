package org.example.clinicarestauracao.Application.Dtos.UserDtos;

import org.example.clinicarestauracao.Domain.Enums.UserRoles;

public record UserRoleUpdateRequestDto(UserRoles role)
{
}
