package org.example.clinicarestauracao.Builders;

import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Domain.Entities.Cargo;

@NoArgsConstructor
public class CargoTestBuilder
{
    private Long id = 1L;
    private String nome = "Monitor";
    private boolean ativo = true;

    public static CargoTestBuilder newCargo()
    {
        return new CargoTestBuilder();
    }

    public CargoTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public CargoTestBuilder setNome(String nome)
    {
        this.nome = nome;
        return this;
    }

    public CargoTestBuilder setAtivo(boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public Cargo build()
    {
        return new Cargo(id, nome, ativo);
    }

    public Cargo buildForCreate()
    {
        return new Cargo(nome);
    }


}
