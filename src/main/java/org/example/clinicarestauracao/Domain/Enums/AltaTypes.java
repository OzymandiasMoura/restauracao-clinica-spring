package org.example.clinicarestauracao.Domain.Enums;

public enum AltaTypes
{
    DESISTENCIA("desistência"),
    CONCLUSAO("conclusão"),
    DESLIGAMENTO("desligamento"),
    OBITO("óbito");

    private final String value;

    AltaTypes(String value)
    {
        this.value = value;
    }
}
