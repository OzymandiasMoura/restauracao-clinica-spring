package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.example.clinicarestauracao.Domain.Validation.TelefoneValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "Triagem")
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_convenio", nullable = false)
    private Convenio convenio;
    @Column(nullable = false, length = 255)
    private String nomePossivelAcolhido;
    @Column(nullable = false, length = 11)
    private String telefone;
    @Column(nullable = false)
    private LocalDateTime dataAgendada;
    @Column(length = 1000)
    private String observacaoEntrevista;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "data_conclusao", nullable = true)
    private LocalDateTime dataConclusao;

    public Triagem(Long id, Convenio convenio, String nomePossivelAcolhido, String telefone, LocalDateTime dataAgendada, String observacaoEntrevista, Status status, LocalDateTime dataConclusao)
    {
        setId(id);
        setConvenioRecuperado(convenio);
        setNomePossivelAcolhido(nomePossivelAcolhido);
        setTelefone(telefone);
        setDataAgendada(dataAgendada);
        setObservacaoEntrevista(observacaoEntrevista);
        validarStatusEDataConclusao(status, dataConclusao);
        setStatus(status);
        setDataConclusao(dataConclusao);
    }

    public Triagem(Convenio convenio, String nomePossivelAcolhido, String telefone, LocalDateTime dataAgendada, String observacaoEntrevista)
    {
        setConvenio(convenio);
        setNomePossivelAcolhido(nomePossivelAcolhido);
        setTelefone(telefone);
        validarDataFutura(dataAgendada);
        setDataAgendada(dataAgendada);
        setObservacaoEntrevista(observacaoEntrevista);
        setStatus(Status.AGENDADA);
        setDataConclusao(null);
    }

    private void setId(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new TriagemWithInvalidInformationException("Id da triagem inválido");
        }

        this.id = id;
    }

    public void setConvenio(Convenio convenio)
    {
        validarAlteracaoPermitida();
        if (convenio == null)
        {
            throw new TriagemWithInvalidInformationException("A triagem precisa estar vinculada a um convenio.");
        }

        if (!convenio.isAtivo())
        {
            throw new TriagemWithInvalidInformationException("Não é possível agendar triagem para um convenio inativo.");
        }

        this.convenio = convenio;
    }

    private void setConvenioRecuperado(Convenio convenio)
    {
        if (convenio == null)
        {
            throw new TriagemWithInvalidInformationException("A triagem precisa estar vinculada a um convenio.");
        }

        this.convenio = convenio;
    }

    public void setNomePossivelAcolhido(String nomePossivelAcolhido)
    {
        validarAlteracaoPermitida();
        if (nomePossivelAcolhido == null || nomePossivelAcolhido.isBlank())
        {
            throw new TriagemWithInvalidInformationException("O nome não pode ser nulo ou vazio.");
        }

        String nomeFormatado = nomePossivelAcolhido.strip();

        if (nomeFormatado.length() < 3)
        {
            throw new TriagemWithInvalidInformationException("O nome do possível acolhido deve ter pelo menos 3 caracteres.");
        }
        if (nomeFormatado.length() > 255)
        {
            throw new TriagemWithInvalidInformationException("O nome do possível acolhido não pode ultrapassar 255 caracteres.");
        }

        this.nomePossivelAcolhido = nomeFormatado;
    }

    private void setDataAgendada(LocalDateTime dataAgendada) {
        if (dataAgendada == null)
        {
            throw new TriagemWithInvalidInformationException("A data agendada não pode ser nula.");
        }

        this.dataAgendada = dataAgendada;
    }

    public void setTelefone(String telefone)
    {
        validarAlteracaoPermitida();
        if (telefone == null || telefone.isBlank())
        {
            throw new TriagemWithInvalidInformationException("O telefone é obrigatório.");
        }

        String telefoneFormatado = TelefoneValidator.normalize(telefone);

        if (!TelefoneValidator.validate(telefoneFormatado))
        {
            throw new TriagemWithInvalidInformationException("Telefone inválido.");
        }

        this.telefone = telefoneFormatado;
    }


    public void setObservacaoEntrevista(String observacaoEntrevista)
    {
        validarAlteracaoPermitida();

        if (observacaoEntrevista == null)
        {
            this.observacaoEntrevista = null;
            return;
        }

        String observacaoFormatada = observacaoEntrevista.trim();

        if (observacaoFormatada.length() > 1000)
        {
            throw new TriagemWithInvalidInformationException("A observação da entrevista não pode ultrapassar 1000 caracteres.");
        }

        this.observacaoEntrevista = observacaoFormatada;
    }

    private void setStatus(Status status)
    {
        if (status == null)
        {
            throw new TriagemWithInvalidInformationException("Status da triagem não pode ser nulo.");
        }
        this.status = status;
    }

    private void setDataConclusao(LocalDateTime date)
    {
        if (date == null)
        {
            this.dataConclusao = null;
            return;
        }
        if (date.isBefore(dataAgendada))
        {
            throw new TriagemWithInvalidInformationException("Data de conclusão não pode ser anterior a data de agendamento.");
        }
        if (date.isAfter(LocalDateTime.now()))
        {
            throw new TriagemWithInvalidInformationException("Data de concluão não pode ser futura.");
        }

        this.dataConclusao = date;
    }

    public void concluir(LocalDateTime dataConclusao)
    {
        if (this.status == Status.CONCLUIDA || this.status == Status.CANCELADA)
        {
            throw new TriagemWithInvalidInformationException("Triagem já concluida ou cancelada.");
        }
        if (dataConclusao == null)
        {
            throw new TriagemWithInvalidInformationException("Data de conclusão não pode ser inválida.");
        }

        setDataConclusao(dataConclusao);
        setStatus(Status.CONCLUIDA);
    }

    public void concluir()
    {
        concluir(LocalDateTime.now());
    }

    public void desconcluir()
    {
        if (this.status != Status.CONCLUIDA)
        {
            throw new TriagemWithInvalidInformationException("Triagem ainda não concluida.");
        }

        setDataConclusao(null);
        setStatus(this.dataAgendada.isBefore(LocalDateTime.now()) ? Status.ATRASADA : Status.AGENDADA);
    }

    public void cancelarTriagem()
    {
        if (this.status == Status.CONCLUIDA)
        {
            throw new TriagemWithInvalidInformationException("Não é possivel cancelar triagem já concluida.");
        }
        if (this.status == Status.CANCELADA)
        {
            throw new TriagemWithInvalidInformationException("Triagem já cancelada.");
        }

        setStatus(Status.CANCELADA);
    }

    public void marcarComoVencida()
    {
        if (this.status == Status.AGENDADA && this.dataAgendada.isBefore(LocalDateTime.now()))
        {
            setStatus(Status.ATRASADA);
        }
    }

    public void reagendar(LocalDateTime novaData)
    {
        if (this.status == Status.CONCLUIDA || this.status == Status.CANCELADA)
        {
            throw new TriagemWithInvalidInformationException("Triagem já concluida ou cancelada.");
        }
        validarDataFutura(novaData);
        setDataAgendada(novaData);
        setStatus(Status.AGENDADA);
    }

    private void validarDataFutura(LocalDateTime data)
    {
        if (data == null)
        {
            throw new TriagemWithInvalidInformationException("A data agendada não pode ser nula.");
        }
        if (!data.isAfter(LocalDateTime.now()))
        {
            throw new TriagemWithInvalidInformationException("A data agendada deve estar no futuro.");
        }
    }

    private void validarStatusEDataConclusao(Status status, LocalDateTime dataConclusao)
    {
        if (status == null)
        {
            throw new TriagemWithInvalidInformationException("Status da triagem não pode ser nulo.");
        }

        if (status == Status.CONCLUIDA && dataConclusao == null)
        {
            throw new TriagemWithInvalidInformationException("Triagem concluída deve possuir data de conclusão.");
        }

        if (status != Status.CONCLUIDA && dataConclusao != null)
        {
            throw new TriagemWithInvalidInformationException("Triagem não concluída não pode possuir data de conclusão.");
        }
    }

    private void validarAlteracaoPermitida()
    {
        if (status == Status.CONCLUIDA || status == Status.CANCELADA)
        {
            throw new TriagemWithInvalidInformationException("Triagem concluída ou cancelada não pode ser alterada.");
        }
    }
}