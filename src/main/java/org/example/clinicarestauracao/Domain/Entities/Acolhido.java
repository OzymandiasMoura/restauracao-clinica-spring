package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CepValidator;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.example.clinicarestauracao.Domain.Validation.TelefoneValidator;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.example.clinicarestauracao.Infrastruct.Database.Converters.EnderecoConverter;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "Acolhidos")
public class Acolhido
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String cpf;
    @Column(nullable = false)
    private String cep;
    @Convert(converter = EnderecoConverter.class)
    @Column(nullable = false, length = 1024)
    private Endereco endereco;
    @Column(nullable = false)
    private String telefone;
    @Column(nullable = false)
    private LocalDate dataNascimento;
    @Column(nullable = false)
    private Boolean ativo;

    public Acolhido(Long id, String nome, String email, String cpf, String cep, Endereco endereco, String telefone, LocalDate dataNascimento, Boolean ativo)
    {
        this.id = id;
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setCep(cep);
        setEndereco(endereco);
        setTelefone(telefone);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
    }

    public Acolhido(String nome, String email, String cpf, String cep, Endereco endereco, String telefone, LocalDate dataNascimento, Boolean ativo)
    {
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setCep(cep);
        setEndereco(endereco);
        setTelefone(telefone);
        setDataNascimento(dataNascimento);
        setAtivo(ativo);
    }

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Nome do acolhido não pode ser nulo ou vazio.");
        }

        String nomeNormalized = nome.strip();

        if (nomeNormalized.length() < 3)
        {
            throw new AcolhidoWithInvalidInformationException("Nome do acolhido deve ter no mínimo 3 caracteres.");
        }
        else if (nomeNormalized.length() > 250)
        {
            throw new AcolhidoWithInvalidInformationException("Nome do acolhido deve ter no máximo 250 caracteres.");
        }
        this.nome = nomeNormalized;
    }

    public void setEmail(String email)
    {
        if (email == null || email.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Email do acolhido não pode ser nulo ou vazio.");
        }

        String emailNormalized = EmailValidator.normalize(email);

        if(!EmailValidator.validate(emailNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("Email do acolhido está invalido.");
        }

        this.email = emailNormalized;
    }

    public void setCpf(String cpf)
    {
        if (cpf == null || cpf.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("CPF do acolhido não pode ser nulo ou vazio.");
        }

        String cpfNormalized = CpfValidator.normalize(cpf);

        if (!CpfValidator.validate(cpfNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("CPF do acolhido está invalido.");
        }
        this.cpf = cpfNormalized;
    }

    public void setCep(String cep)
    {
        if (cep == null || cep.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("CEP do acolhido não pode ser nulo ou vazio.");
        }

        String cepNormalized = CepValidator.normalize(cep);
        if (!CepValidator.validate(cepNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("CEP do acolhido está invalido.");
        }

        this.cep = cepNormalized;
    }

    public void setEndereco(Endereco endereco)
    {
        if (endereco == null)
        {
            throw new AcolhidoWithInvalidInformationException("Endereço do acolhido não pode ser nulo ou vazio.");
        }

        this.endereco = endereco;
    }

    public void setTelefone(String telefone)
    {
        if (telefone == null || telefone.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido não pode ser nulo ou vazio.");
        }

        String telefoneNormalized = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido está invalido.");
        }

        this.telefone = telefoneNormalized;
    }

    public void setDataNascimento(LocalDate dataNascimento)
    {
        if (dataNascimento == null)
        {
            throw new AcolhidoWithInvalidInformationException("É necessário definir a data de nascimento.");
        }

        if (dataNascimento.isAfter(LocalDate.now()))
        {
            throw new AcolhidoWithInvalidInformationException("Data de nascimento não pode ser futura.");
        }

        if (dataNascimento.isBefore(LocalDate.of(1900, 1, 1)))
        {
            throw new AcolhidoWithInvalidInformationException("Data de nascimento não pode ser anterior a 1900.");
        }

        this.dataNascimento = dataNascimento;
    }
}
