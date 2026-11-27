package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.example.clinicarestauracao.Domain.Enums.Etnia;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AcolhidoTestBuilder
{
    private Long id = 1L;
    private String nome = "Maria Silva";
    private String email = "maria@email.com";
    private String cpf = "52998224725";
    private Boolean moradorRua = false;
    private String cep = "01001000";
    private Endereco endereco = EnderecoTestBuilder.newEndereco().build();
    private String telefone = "11999999999";
    private String telefone2;
    private LocalDate dataNascimento = LocalDate.of(1999, 9, 9);
    private Etnia etnia = Etnia.NAO_INFORMADO;
    private String genero = "Não informado.";
    private String orientacaoSexual = "Não informado.";
    private LocalDateTime dataCriacao = LocalDateTime.of(2020, 1, 2, 3, 4);
    private Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().build();
    private String grauParentesco = "Filha";
    private Boolean ativo = true;

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

    public AcolhidoTestBuilder setMoradorRua(Boolean moradorRua)
    {
        this.moradorRua = moradorRua;
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

    public AcolhidoTestBuilder setTelefone2(String telefone2)
    {
        this.telefone2 = telefone2;
        return this;
    }

    public AcolhidoTestBuilder setDataNascimento(LocalDate dataNascimento)
    {
        this.dataNascimento = dataNascimento;
        return this;
    }

    public AcolhidoTestBuilder setEtnia(Etnia etnia)
    {
        this.etnia = etnia;
        return this;
    }

    public AcolhidoTestBuilder setGenero(String genero)
    {
        this.genero = genero;
        return this;
    }

    public AcolhidoTestBuilder setOrientacaoSexual(String orientacaoSexual)
    {
        this.orientacaoSexual = orientacaoSexual;
        return this;
    }

    public AcolhidoTestBuilder setDataCriacao(LocalDateTime dataCriacao)
    {
        this.dataCriacao = dataCriacao;
        return this;
    }

    public AcolhidoTestBuilder setResponsavel(Responsavel responsavel)
    {
        this.responsavel = responsavel;
        return this;
    }

    public AcolhidoTestBuilder setGrauParentesco(String grauParentesco)
    {
        this.grauParentesco = grauParentesco;
        return this;
    }

    public AcolhidoTestBuilder setAtivo(Boolean ativo)
    {
        this.ativo = ativo;
        return this;
    }

    public Acolhido build()
    {
        return new Acolhido(id, dataCriacao, nome, email, cpf, moradorRua, cep, endereco, telefone, telefone2, dataNascimento, etnia, genero, orientacaoSexual, responsavel, grauParentesco, ativo);
    }

    public Acolhido buildForCreate()
    {
        return new Acolhido(nome, email, cpf, moradorRua, cep, endereco, telefone, telefone2, dataNascimento, etnia, genero, orientacaoSexual, responsavel, grauParentesco);
    }

}
