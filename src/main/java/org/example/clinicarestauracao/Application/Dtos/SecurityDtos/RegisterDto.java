package org.example.clinicarestauracao.Application.Dtos.SecurityDtos;

import org.example.clinicarestauracao.Domain.Enums.UserRoles;

public record RegisterDto(String username, String password, UserRoles role)
{
}
