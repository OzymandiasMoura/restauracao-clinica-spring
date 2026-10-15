package org.example.clinicarestauracao.Domain.Enums;

public enum TreatmentStage
{
    INICIO("inicio"),
    MEIO("meio"),
    FIM("fim");

    private final String value;

    TreatmentStage(String value)
    {
        this.value = value;
    }
}
