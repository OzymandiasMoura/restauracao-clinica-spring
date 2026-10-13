package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

import java.time.LocalDate;

public class AcolhidoTestBuilder
{
    private Long id = 1L;
    private String nome = "Maria Silva";
    private String email = "maria@email.com";
    private String cpf = "52998224725";
    private String cep = "01001000";
    private Endereco endereco = EnderecoTestBuilder.newEndereco().build();
    private String telefone = "11999999999";
    private LocalDate dataNascimento = LocalDate.of(1999, 9, 9);

    public static AcolhidoTestBuilder newAcolhido()
    {
        return new AcolhidoTestBuilder();
    }

    public AcolhidoTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public AcolhidoTestBuilder setNome(String nome)
    {
        this.nome = nome;
        return this;
    }

    public AcolhidoTestBuilder setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public AcolhidoTestBuilder setCpf(String cpf)
    {
        this.cpf = cpf;
        return this;
    }

    public AcolhidoTestBuilder setCep(String cep)
    {
        this.cep = cep;
        return this;
    }

    public AcolhidoTestBuilder setEndereco(Endereco endereco)
    {
        this.endereco = endereco;
        return this;
    }

    public AcolhidoTestBuilder setTelefone(String telefone)
    {
        this.telefone = telefone;
        return this;
    }

    public AcolhidoTestBuilder setDataNascimento(LocalDate dataNascimento)
    {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public Acolhido build()
    {
        return new Acolhido( id, nome, email, cpf, cep, endereco, telefone, dataNascimento);
    }

    public Acolhido buildForCreate()
    {
        return new Acolhido(nome, email, cpf, cep, endereco, telefone, dataNascimento);
    }

}
