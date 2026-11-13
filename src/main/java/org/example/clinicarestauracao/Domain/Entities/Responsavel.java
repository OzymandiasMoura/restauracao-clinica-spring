package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Validation.CepValidator;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.example.clinicarestauracao.Domain.Validation.TelefoneValidator;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.example.clinicarestauracao.Infrastruct.Database.Converters.EnderecoConverter;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@Table(name = "Responsaveis")
@NoArgsConstructor
public class Responsavel
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
    private String telefone;
    @Column(nullable = false, length = 1024)
    @Convert(converter = EnderecoConverter.class)
    private Endereco endereco;
    @Column(nullable = false)
    private String cep;
    @Column(nullable = false)
    private boolean ativo;

    public Responsavel(Long id, String nome, String email, String cpf, String telefone, Endereco endereco, String cep,  boolean ativo)
    {
        setId(id);
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setTelefone(telefone);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(ativo);
    }

    public Responsavel(String nome, String email, String cpf, String telefone, Endereco endereco, String cep)
    {
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setTelefone(telefone);
        setEndereco(endereco);
        setCep(cep);
        setAtivo(true);
    }

    private void setId(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new ResponsavelWithInvalidInformationException("Id não pode ser nulo ou menor ou igual a zero.");
        }
        this.id = id;
    }

    public void setNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new ResponsavelWithInvalidInformationException("Nome não pode ser nulo ou vazio");
        }

        String nomeNormalized = nome.strip();

        if (nomeNormalized.length() < 3)
        {
            throw new ResponsavelWithInvalidInformationException("Nome do responsável deve ter no mínimo 3 caracteres.");
        } else if (nomeNormalized.length() > 250)
        {
            throw new ResponsavelWithInvalidInformationException("Nome do responsável deve ter no máximo 250 caracteres.");
        }

        this.nome = nomeNormalized;
    }

    public void setEmail(String email)
    {
        if (email == null || email.isBlank())
        {
            throw new ResponsavelWithInvalidInformationException("Email do responsável não pode ser nulo ou vazio");
        }

        String emailNormalized = EmailValidator.normalize(email);

        if(!EmailValidator.validate(emailNormalized))
        {
            throw new ResponsavelWithInvalidInformationException("Email é inválido.");
        }

        this.email = emailNormalized;
    }

    public void setCpf(String cpf)
    {
        if (cpf == null || cpf.isBlank())
        {
            throw new ResponsavelWithInvalidInformationException("CPF do responsável não pode ser nulo ou vazio");
        }

        String cpfNormalized = CpfValidator.normalize(cpf);

        if(!CpfValidator.validate(cpfNormalized))
        {
            throw new ResponsavelWithInvalidInformationException("CPF inválido.");
        }

        this.cpf = cpfNormalized;
    }

    public void setTelefone(String telefone)
    {
        if (telefone == null || telefone.isBlank())
        {
            throw new ResponsavelWithInvalidInformationException("Telefone não pode ser nulo ou vazio");
        }

        String telefoneNormalized = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneNormalized))
        {
            throw new ResponsavelWithInvalidInformationException("Telefone inválido.");
        }

        this.telefone = telefoneNormalized;
    }

    public void setEndereco(Endereco endereco)
    {
        if (endereco == null)
        {
            throw new ResponsavelWithInvalidInformationException("Endereço do responsável não pode ser nulo.");
        }

        this.endereco = endereco;
    }

    public void setCep(String cep)
    {
        if (cep == null || cep.isBlank())
        {
            throw new ResponsavelWithInvalidInformationException("CEP não pode ser nulo ou vazio.");
        }
        String cepNormalized = CepValidator.normalize(cep);

        if(!CepValidator.validate(cepNormalized))
        {
            throw new ResponsavelWithInvalidInformationException("CEP inválido.");
        }

        this.cep = cepNormalized;
    }

    private void setAtivo(boolean ativo)
    {
        this.ativo = ativo;
    }

    public void deactivateResponsavel()
    {
        if(!this.ativo)
        {
            throw new ResponsavelWithInvalidInformationException("Responsável já está desativado.");
        }
        setAtivo(false);
    }

    public void reactivateResponsavel()
    {
        if (this.ativo)
        {
            throw new ResponsavelWithInvalidInformationException("Responsável já está ativado.");
        }

        setAtivo(true);
    }
}



