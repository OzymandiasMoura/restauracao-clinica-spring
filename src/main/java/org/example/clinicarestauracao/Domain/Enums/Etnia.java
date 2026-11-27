package org.example.clinicarestauracao.Domain.Enums;

public enum Etnia
{
    PRETO ("preto"),
    PARDO ("pardo"),
    INDIGENA ("indígena"),
    BRANCO ("branco"),
    AMARELO ("amarelo"),
    NAO_INFORMADO ("não informado");

    private final String value;

    Etnia(String value)
    {
        this.value = value;
    }
}
