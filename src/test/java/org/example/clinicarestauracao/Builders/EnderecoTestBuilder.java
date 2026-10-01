package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

public class EnderecoTestBuilder
{
    private String logradouro = "Rua das Flores";
    private Integer numero = 123;
    private String bairro = "Centro";
    private String cidade = "São Paulo";
    private String estado = "SP";
    private String complemento = "Apartamento 42";

    public static EnderecoTestBuilder newEndereco()
    {
        return new EnderecoTestBuilder();
    }

    public EnderecoTestBuilder setLogradouro(String logradouro)
    {
        this.logradouro = logradouro;
        return this;
    }

    public EnderecoTestBuilder setNumero(Integer numero)
    {
        this.numero = numero;
        return this;
    }

    public EnderecoTestBuilder setComplemento(String complemento)
    {
        this.complemento = complemento;
        return this;
    }

    public EnderecoTestBuilder setBairro(String bairro)
    {
        this.bairro = bairro;
        return this;
    }

    public EnderecoTestBuilder setCidade(String cidade)
    {
        this.cidade = cidade;
        return this;
    }

    public EnderecoTestBuilder setEstado(String estado)
    {
        this.estado = estado;
        return this;
    }

    public Endereco build()
    {
        return new Endereco(logradouro, numero, bairro, cidade, estado, complemento);
    }

    public Endereco buildWithoutComplemento()
    {
        return new Endereco(logradouro, numero, bairro, cidade, estado);
    }

}
