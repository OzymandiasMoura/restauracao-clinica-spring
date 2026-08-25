package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.clinicarestauracao.Application.Exceptions.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CepValidator;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "Funcionarios")
public class Funcionario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(unique = true, nullable = false)
    private String cpf;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private LocalDate dataNascimento;
    @Column(nullable = false)
    private String endereco;
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private boolean ativo;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Funcionario(Long id, String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, boolean ativo, User user)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(ativo);
        setUser(user);
    }

    public Funcionario(String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, User user)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(true);
        setUser(user);
    }

    public Funcionario(Long id, String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, boolean ativo)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(ativo);
    }

    public Funcionario(String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(true);
    }

    private void setId(Long id)
    {
        this.id = id;
    }

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("Funcionário não pode ter o nome vazio ou em branco.");
        }

        String nomeNormalizado = nome.strip();

        if (nomeNormalizado.length() < 3)
        {
            throw new FuncionarioWithInvalidInformationException("Nome deve ter no mínimo 3 caracteres.");
        } else
        {
            this.nome = nomeNormalizado;
        }
    }

    public void setCpf(String cpf)
    {
        String cpfNormalizado = CpfValidator.normalize(cpf);

        if (cpfNormalizado == null || cpfNormalizado.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("CPF não pode ser vazio ou em branco.");
        }
        if (!CpfValidator.validate(cpfNormalizado))
        {
            throw new FuncionarioWithInvalidInformationException("CPF é inválido.");
        } else
        {
            this.cpf = cpfNormalizado;
        }
    }

    public void setEmail(String email)
    {
        if (email == null || email.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("E-mail não pode ser vazio ou em branco.");
        }

        String emailNormalizado = EmailValidator.normalize(email);

        if (!EmailValidator.validate(emailNormalizado))
        {
            throw new FuncionarioWithInvalidInformationException("E-mail é inválido.");
        } else
        {
            this.email = emailNormalizado;
        }
    }

    public void setDataNascimento(LocalDate dataNascimento)
    {
        if (dataNascimento == null)
        {
            throw new FuncionarioWithInvalidInformationException("É necessário definir a data de nascimento.");
        } else if (dataNascimento.isAfter(LocalDate.now()))
        {
            throw new FuncionarioWithInvalidInformationException("Data de nascimento não pode ser futura.");
        } else
        {
            this.dataNascimento = dataNascimento;
        }
    }

    public void setEndereco(String endereco)
    {
        if (endereco == null || endereco.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("Endereço não pode ser nulo ou vazio.");
        }
        this.endereco = endereco.strip();
    }

    public void setCep(String cep)
    {
        if (cep == null || cep.isBlank())
        {
            throw new FuncionarioWithInvalidInformationException("CEP não pode ser nulo ou vazio.");
        }

        String cepNormalizado = CepValidator.normalize(cep);

        if (!CepValidator.validate(cepNormalizado))
        {
            throw new FuncionarioWithInvalidInformationException("CEP é inválido.");
        }
        this.cep = cepNormalizado;
    }
}
