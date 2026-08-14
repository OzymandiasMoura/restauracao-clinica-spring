package org.example.clinicarestauracao.Domain.Enums;

import lombok.Getter;

@Getter
public enum UserRoles
{
    ADMIN("admin"),
    USER("user");

    private final String value;

    UserRoles(String value)
    {
        this.value = value;
    }
}
