package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
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
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;
    @Column(nullable = false)
    private LocalDate dataAdmissao;
    @Column(nullable = true)
    private LocalDate dataDemissao;

    public Funcionario(Long id, String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, boolean ativo, User user, Cargo cargo, LocalDate dataAdmissao)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setDataAdmissao(dataAdmissao);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(ativo);
        setUser(user);
        setCargo(cargo);
    }

    public Funcionario(String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, User user, Cargo cargo, LocalDate dataAdmissao)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setDataAdmissao(dataAdmissao);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(true);
        setUser(user);
        setCargo(cargo);
    }

    public Funcionario(Long id, String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, boolean ativo, Cargo cargo, LocalDate dataAdmissao)
    {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setDataAdmissao(dataAdmissao);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(ativo);
        setCargo(cargo);

    }

    public Funcionario(String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, Cargo cargo,  LocalDate dataAdmissao)
    {
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setDataNascimento(dataNascimento);
        setDataAdmissao(dataAdmissao);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(true);
        setCargo(cargo);

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
        validarDataNascimento(dataNascimento);

        if (this.dataAdmissao != null)
        {
            validarOrdemDasDatas(dataNascimento, this.dataAdmissao, this.dataDemissao);
        }

        this.dataNascimento = dataNascimento;
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

    public void setCargo(Cargo cargo)
    {
        if (cargo == null)
        {
            throw new FuncionarioWithInvalidInformationException("Cargo do funcionário deve ser informado.");
        }
        this.cargo = cargo;
    }

    public void setDataAdmissao(LocalDate dataAdmissao)
    {
        validarDataAdmissao(dataAdmissao);

        if (this.dataNascimento != null)
        {
            validarOrdemDasDatas(this.dataNascimento, dataAdmissao, this.dataDemissao);
        }

        this.dataAdmissao = dataAdmissao;
    }

    private void setDataDemissao(LocalDate dataDemissao)
    {
        if (dataDemissao.isBefore(dataAdmissao) ||  dataDemissao.isEqual(dataAdmissao))
        {
            throw new FuncionarioWithInvalidInformationException("Data de demissão não pode ser anterior ou igual a data de admissão.");
        }
        if (dataDemissao.isAfter(LocalDate.now()))
        {
            throw new FuncionarioWithInvalidInformationException("Data de demissão não pode ser futura.");
        }

        this.dataDemissao = dataDemissao;
    }

    public void demitir(LocalDate dataDemissao)
    {
        if (dataDemissao == null)
        {
            throw new FuncionarioWithInvalidInformationException("Data de demissão deve ser informada.");
        }

        setDataDemissao(dataDemissao);
        setAtivo(false);
    }

    public void reactivate()
    {
        this.dataDemissao = null;
        setAtivo(true);
    }

    private void validarDataNascimento(LocalDate dataNascimento)
    {
        if (dataNascimento == null)
        {
            throw new FuncionarioWithInvalidInformationException("É necessário definir a data de nascimento.");
        }

        if (dataNascimento.isAfter(LocalDate.now()))
        {
            throw new FuncionarioWithInvalidInformationException("Data de nascimento não pode ser futura.");
        }
    }

    private void validarDataAdmissao(LocalDate dataAdmissao)
    {
        if (dataAdmissao == null)
        {
            throw new FuncionarioWithInvalidInformationException("Data de admissão não pode ser nula.");
        }

        if (dataAdmissao.isAfter(LocalDate.now()))
        {
            throw new FuncionarioWithInvalidInformationException("Data de admissão não pode ser futura.");
        }
    }

    private void validarOrdemDasDatas(LocalDate dataNascimento, LocalDate dataAdmissao, LocalDate dataDemissao)
    {
        if (dataAdmissao.isBefore(dataNascimento))
        {
            throw new FuncionarioWithInvalidInformationException("Data de admissão não pode ser anterior ao nascimento.");
        }

        if (dataDemissao != null
                && !dataAdmissao.isBefore(dataDemissao))
        {
            throw new FuncionarioWithInvalidInformationException("Data de admissão deve ser anterior à data de demissão.");
        }
    }

    public void atualizarDatas(LocalDate novaDataNascimento, LocalDate novaDataAdmissao)
    {
        validarDataNascimento(novaDataNascimento);
        validarDataAdmissao(novaDataAdmissao);
        validarOrdemDasDatas(novaDataNascimento, novaDataAdmissao, this.dataDemissao);

        this.dataNascimento = novaDataNascimento;
        this.dataAdmissao = novaDataAdmissao;
    }

}
