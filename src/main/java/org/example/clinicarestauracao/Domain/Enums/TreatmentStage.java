package org.example.clinicarestauracao.Domain.Enums;

public enum TreatmentStage
{
    EM_TRATAMENTO("emTratamento"),
    PROXIMO_ALTA("proximoAlta"),
    ALTA_VENCIDA("altaVencida"),
    ALTA_CONCEDIDA("altaConcedida")
    ;

    private final String value;

    TreatmentStage(String value)
    {
        this.value = value;
    }
}
