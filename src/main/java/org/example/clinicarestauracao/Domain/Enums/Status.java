package org.example.clinicarestauracao.Domain.Enums;

public enum Status {
    AGENDADA("agendada"),
    ATRASADA("atrasada"),
    CONCLUIDA("concluida"),
    CANCELADA("cancelada");

    private final String value;

    Status(String value)
    {
        this.value = value;
    }
}
