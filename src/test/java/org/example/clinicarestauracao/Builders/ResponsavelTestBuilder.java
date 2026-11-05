package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

public class ResponsavelTestBuilder
{
    private Long id = 1L;
    private String nome = "João da Silva";
    private String email = "joao.silva@email.com";
    private String cpf = "16899535009";
    private String telefone = "11999999999";
    private Endereco endereco = EnderecoTestBuilder.newEndereco().build();
    private String cep = "01001000";
    private Boolean ativo = true;

    public static ResponsavelTestBuilder newResponsavel()
    {
        return new ResponsavelTestBuilder();
    }

    public ResponsavelTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public ResponsavelTestBuilder setNome(String nome)
    {
        this.nome = nome;
        return this;
    }

    public ResponsavelTestBuilder setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public ResponsavelTestBuilder setCpf(String cpf)
    {
        this.cpf = cpf;
        return this;
    }

    public ResponsavelTestBuilder setTelefone(String telefone)
    {
        this.telefone = telefone;
        return this;
    }

    public ResponsavelTestBuilder setEndereco(Endereco endereco)
    {
        this.endereco = endereco;
        return this;
    }

    public ResponsavelTestBuilder setCep(String cep)
    {
        this.cep = cep;
        return this;
    }

    public ResponsavelTestBuilder setAtivo(Boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public Responsavel build()
    {
        return new Responsavel(id, nome, email, cpf, telefone, endereco, cep,  ativo);
    }

    public Responsavel buildForCreate()
    {
        return new Responsavel(nome, email, cpf, telefone, endereco, cep);
    }
}
