package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;

import java.time.LocalDate;

public class FuncionarioTestBuilder
{
    private Long id = 1L;
    private String nome = "Pedro Moura";
    private String cpf = "52998224725";
    private String email = "pedro@email.com";
    private LocalDate dataNascimento = LocalDate.of(1990, 1, 10);
    private boolean ativo = true;
    private User user = null;

    public static FuncionarioTestBuilder newFuncionario()
    {
        return new FuncionarioTestBuilder();
    }

    public FuncionarioTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public FuncionarioTestBuilder setNome(String nome)
    {
        this.nome = nome;
        return this;
    }

    public FuncionarioTestBuilder setCpf(String cpf)
    {
        this.cpf = cpf;
        return this;
    }

    public FuncionarioTestBuilder setEmail(String email)
    {
        this.email = email;
        return this;
    }

    public FuncionarioTestBuilder setDataNascimento(LocalDate dataNascimento)
    {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public FuncionarioTestBuilder setAtivo(boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public FuncionarioTestBuilder setUser(User user)
    {
        this.user = user;
        return this;
    }

    public Funcionario build()
    {
        return new Funcionario(id, nome, cpf, email, dataNascimento, ativo, user);
    }

    public Funcionario buildForCreate()
    {
        return new Funcionario(nome, cpf, email, dataNascimento, ativo, user);
    }
}
