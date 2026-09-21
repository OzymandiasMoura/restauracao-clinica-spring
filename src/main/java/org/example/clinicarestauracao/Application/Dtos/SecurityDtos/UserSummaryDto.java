package org.example.clinicarestauracao.Application.Dtos.SecurityDtos;

import org.example.clinicarestauracao.Domain.Enums.UserRoles;

public record UserSummaryDto(Long id, String username, UserRoles role)
{
}
