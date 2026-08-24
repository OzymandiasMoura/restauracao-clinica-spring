package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Funcionarios")
public class Funcionario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(unique = true, nullable = false)
    private String cpf;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private LocalDate dataNascimento;
    @Column(unique = true, nullable = false)
    private boolean ativo;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Funcionario(Long id, String nome, String cpf,  String email, LocalDate dataNascimento, Boolean ativo, User user)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
        setUser(user);
    }

    public Funcionario(String nome, String cpf,  String email, LocalDate dataNascimento, Boolean ativo, User user)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
        setUser(user);
    }

    public Funcionario(Long id, String nome, String cpf,  String email, LocalDate dataNascimento, Boolean ativo)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
    }

    public Funcionario(String nome, String cpf,  String email, LocalDate dataNascimento, Boolean ativo)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
    }

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("Funcionário não pode ter o nome vazio ou em branco.");
        }
        if (nome.length() < 3)
        {
            throw new FuncionarioWithInvalidInformationException("Nome deve ter no mínimo 3 caracteres.");
        }
        else
        {
            this.nome = nome;
        }
    }

    public void setCpf(String cpf)
    {
        String cpfNormalizado = CpfValidator.normalize(cpf);

        if(cpfNormalizado == null || cpfNormalizado.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("CPF não pode ser vazio ou em branco.");
        }
        if(!CpfValidator.validate(cpfNormalizado))
        {
            throw new FuncionarioWithInvalidInformationException("CPF é inválido.");
        }
        else
        {
            this.cpf = cpfNormalizado;
        }
    }
}
