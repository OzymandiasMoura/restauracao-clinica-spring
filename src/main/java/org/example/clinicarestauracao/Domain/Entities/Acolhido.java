package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.Etnia;
import org.example.clinicarestauracao.Domain.Validation.CepValidator;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.example.clinicarestauracao.Domain.Validation.TelefoneValidator;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.example.clinicarestauracao.Infrastruct.Database.Converters.EnderecoConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime dataCriacao;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = true, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String cpf;
    @Column(nullable = false)
    private Boolean moradorRua;
    @Column(nullable = true)
    private String cep;
    @Convert(converter = EnderecoConverter.class)
    @Column(nullable = true, length = 1024)
    private Endereco endereco;
    @Column(nullable = true)
    private String telefone;
    @Column(nullable = true)
    private String telefone2;
    @Column(nullable = false)
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Etnia etnia;
    @Column(nullable = false)
    private String genero;
    @Column(nullable = false)
    private String orientacaoSexual;
    @ManyToOne(optional = false)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;
    @Column(nullable = false)
    private String grauParentesco;
    @Column(nullable = false)
    private Boolean ativo;

    public Acolhido(Long id, LocalDateTime dataCriacao, String nome, String email, String cpf, Boolean moradorRua, String cep, Endereco endereco, String telefone, String telefone2, LocalDate dataNascimento, Etnia etnia, String genero, String orientacaoSexual, Responsavel responsavel, String grauParentesco, Boolean ativo)
    {
        setId(id);
        setDataCriacao(dataCriacao);
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        updateHousingStatus(moradorRua, cep, endereco);
        setTelefone(telefone);
        setTelefone2(telefone2);
        setDataNascimento(dataNascimento);
        setEtnia(etnia);
        setGenero(genero);
        setOrientacaoSexual(orientacaoSexual);
        setResponsavel(responsavel);
        setGrauParentesco(grauParentesco);
        setAtivo(ativo);
    }

    public Acolhido(String nome, String email, String cpf, Boolean moradorRua, String cep, Endereco endereco, String telefone, String telefone2, LocalDate dataNascimento, Etnia etnia, String genero, String orientacaoSexual, Responsavel responsavel, String grauParentesco)
    {
        setDataCriacao(LocalDateTime.now());
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        updateHousingStatus(moradorRua, cep, endereco);
        setTelefone(telefone);
        setTelefone2(telefone2);
        setDataNascimento(dataNascimento);
        setEtnia(etnia);
        setGenero(genero);
        setOrientacaoSexual(orientacaoSexual);
        setResponsavelCreate(responsavel);
        setGrauParentesco(grauParentesco);
        setAtivo(true);
    }

    private void setId(Long id)
    {
        if (id== null || id <= 0)
        {
            throw new AcolhidoWithInvalidInformationException("Id inválido.");
        }

        this.id = id;
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

    private void setDataCriacao(LocalDateTime dataCriacao)
    {
        if (dataCriacao == null)
        {
            throw new AcolhidoWithInvalidInformationException("Data de criação do acolhido deve ser informada.");
        }
        this.dataCriacao = dataCriacao;
    }

    public void setEmail(String email)
    {
        if (email == null)
        {
            this.email = null;
            return;
        }
        if (email.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Email do acolhido não pode ser vazio.");
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

    private void setMoradorRua(Boolean moradorRua)
    {
        if (moradorRua == null)
        {
            throw new AcolhidoWithInvalidInformationException("Deve marcar se acolhido possui moradia fixa.");
        }

        this.moradorRua = moradorRua;
    }

    private void setCep(String cep)
    {
        if(moradorRua == null)
        {
            throw new AcolhidoWithInvalidInformationException("Para definir endereço primeiro deve informar se o acolhido está em situação de rua.");
        }
        if (cep == null && moradorRua)
        {
            this.cep = null;
            return;
        }
        if ((cep == null || cep.isBlank()) && !moradorRua)
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

    private void setEndereco(Endereco endereco)
    {
        if(moradorRua == null)
        {
            throw new AcolhidoWithInvalidInformationException("Para definir endereço primeiro deve informar se o acolhido está em situação de rua.");
        }
        if (endereco == null && moradorRua)
        {
            this.endereco = null;
            return;
        }
        if (endereco == null && !moradorRua)
        {
            throw new AcolhidoWithInvalidInformationException("Endereço do acolhido não pode ser nulo ou vazio.");
        }

        this.endereco = endereco;
    }

    public void setTelefone(String telefone)
    {
        if (telefone == null)
        {
            this.telefone = null;
            return;
        }
        if (telefone.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido não pode ser nulo ou vazio.");
        }

        String telefoneNormalized = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido está invalido.");
        }
        if (telefoneNormalized.equals(this.telefone2))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone não pode ser igual ao telefone 2.");
        }

        this.telefone = telefoneNormalized;
    }

    public void setTelefone2(String telefone)
    {
        if (telefone == null)
        {
            this.telefone2 = null;
            return;
        }
        if (telefone.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido não pode ser nulo ou vazio.");
        }

        String telefoneNormalized = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneNormalized))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido está invalido.");
        }

        if (telefoneNormalized.equals(this.telefone))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone 2 não pode ser igual ao telefone.");
        }

        this.telefone2 = telefoneNormalized;
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
        if (dataNascimento.isAfter(LocalDate.now().minusYears(18)))
        {
            throw new AcolhidoWithInvalidInformationException("Acolhido não pode ter menos que 18 anos.");
        }

        this.dataNascimento = dataNascimento;
    }

    public void setEtnia(Etnia etnia)
    {
        if (etnia == null)
        {
            this.etnia = Etnia.NAO_INFORMADO;
            return;
        }

        this.etnia = etnia;
    }

    public void setGenero(String genero)
    {
        if (genero == null || genero.isBlank())
        {
            this.genero = "Não informado.";
            return;
        }

        String formatted = genero.strip();

        if (formatted.isBlank() || formatted.length() < 3)
        {
            throw new AcolhidoWithInvalidInformationException("Gênero deve ter pelo menos 3 caracteres.");
        }

        this.genero = formatted;
    }

    public void setOrientacaoSexual(String orientacaoSexual)
    {
        if (orientacaoSexual == null || orientacaoSexual.isBlank())
        {
            this.orientacaoSexual = "Não informado.";
            return;
        }

        String formatted = orientacaoSexual.strip();

        if (formatted.isBlank() || formatted.length() < 3)
        {
            throw new AcolhidoWithInvalidInformationException("Orientação sexual deve ter pelo menos 3 caracteres.");
        }

        this.orientacaoSexual = formatted;
    }

    public void setResponsavel(Responsavel responsavel)
    {
        if (responsavel == null)
        {
            throw new AcolhidoWithInvalidInformationException("Responsável do acolhido não pode ser nulo.");
        }

        this.responsavel = responsavel;
    }

    public void setResponsavelCreate(Responsavel responsavel)
    {
        if (responsavel == null)
        {
            throw new AcolhidoWithInvalidInformationException("Responsável do acolhido não pode ser nulo.");
        }
        if (!responsavel.isAtivo())
        {
            throw new AcolhidoWithInvalidInformationException("Responsável do acolhido não pode ser inativo.");
        }

        this.responsavel = responsavel;
    }

    public void setGrauParentesco(String grauParentesco)
    {
        if (grauParentesco == null || grauParentesco.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("O gau de parentesco entre acolhido e responsável não pode ser nulo ou vazio.");
        }

        this.grauParentesco = grauParentesco;
    }

    private void setAtivo(Boolean ativo)
    {
        if (ativo == null)
        {
            throw new AcolhidoWithInvalidInformationException("Acolhido não pode ter estado de ativo nulo.");
        }
        this.ativo = ativo;
    }

    public void deactivate()
    {
        if (this.ativo == false)
        {
            throw new AcolhidoWithInvalidInformationException("Acolhido já desativado.");
        }

        setAtivo(false);
    }

    public void reactivate()
    {
        if(this.ativo == true)
        {
            throw new AcolhidoWithInvalidInformationException("Acolhido já ativado.");
        }

        setAtivo(true);
    }

    public void updateHousingStatus(Boolean moradorRua, String cep, Endereco endereco)
    {
        if (moradorRua == null)
        {
            throw new AcolhidoWithInvalidInformationException("Para definir endereço primeiro deve informar se o acolhido está em situação de rua.");
        }
        if ((moradorRua && cep != null) || (moradorRua && endereco != null))
        {
            throw new AcolhidoWithInvalidInformationException("Não é possível definir CEP e endereço se o acolhido se encontra em situação de rua.");
        }
        if((!moradorRua && cep == null) || (!moradorRua && endereco == null))
        {
            throw new AcolhidoWithInvalidInformationException("CEP e endereço não podem ser nulos enquanto o acolhido não se encontra em situação de rua.");
        }
        if (moradorRua)
        {
            setMoradorRua(true);
            setCep(null);
            setEndereco(null);
        }
        if (!moradorRua)
        {
            String cepValidado = CepValidator.normalize(cep);
            if (!CepValidator.validate(cepValidado))
            {
                throw new AcolhidoWithInvalidInformationException("CEP do acolhido está inválido.");
            }
            setMoradorRua(false);
            setCep(cepValidado);
            setEndereco(endereco);
        }
    }

    private String normalizeOptionalTelefone(String telefone)
    {
        if (telefone == null)
        {
            return null;
        }

        if (telefone.isBlank())
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido não pode ser nulo ou vazio.");
        }

        String telefoneNormalizado = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneNormalizado))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone do acolhido está invalido.");
        }

        return telefoneNormalizado;
    }

    public void updateTelefones(String telefone, String telefone2)
    {
        String telefoneNormalizado = normalizeOptionalTelefone(telefone);
        String telefone2Normalizado = normalizeOptionalTelefone(telefone2);

        if (telefoneNormalizado != null && telefoneNormalizado.equals(telefone2Normalizado))
        {
            throw new AcolhidoWithInvalidInformationException("Telefone não pode ser igual ao telefone 2.");
        }

        this.telefone = telefoneNormalizado;
        this.telefone2 = telefone2Normalizado;
    }
}
