package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.Status;

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

    @Column(nullable = false)
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

    public Triagem(
            Convenio convenio,
            String nomePossivelAcolhido,
            String telefone,
            LocalDateTime dataAgendada,
            String observacaoEntrevista
    ) {
        setConvenio(convenio);
        setNomePossivelAcolhido(nomePossivelAcolhido);
        setTelefone(telefone);
        setDataAgendada(dataAgendada);

        this.observacaoEntrevista =
                observacaoEntrevista == null
                        ? null
                        : observacaoEntrevista.trim();

        this.status = Status.AGENDADA;
    }

    public void setConvenio(Convenio convenio) {
        if (convenio == null) {
            throw new TriagemWithInvalidInformationException(
                    "A triagem precisa estar vinculada a um convenio."
            );
        }

        if (!convenio.isAtivo()) {
            throw new TriagemWithInvalidInformationException(
                    "Não é possível agendar triagem para um convenio inativo."
            );
        }

        this.convenio = convenio;
    }

    public void setNomePossivelAcolhido(String nomePossivelAcolhido) {
        if (nomePossivelAcolhido == null || nomePossivelAcolhido.isBlank()) {
            throw new TriagemWithInvalidInformationException(
                    "O nome não pode ser nulo ou vazio."
            );
        }

        String nomeFormatado = nomePossivelAcolhido.trim();

        if (nomeFormatado.length() < 3) {
            throw new TriagemWithInvalidInformationException(
                    "O nome do possível acolhido deve ter pelo menos 3 caracteres."
            );
        }

        this.nomePossivelAcolhido = nomeFormatado;
    }

    public void setDataAgendada(LocalDateTime dataAgendada) {
        if (dataAgendada == null) {
            throw new TriagemWithInvalidInformationException(
                    "A data agendada não pode ser nula."
            );
        }

        if (!dataAgendada.isAfter(LocalDateTime.now())) {
            throw new TriagemWithInvalidInformationException(
                    "A data agendada deve estar no futuro."
            );
        }

        this.dataAgendada = dataAgendada;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new TriagemWithInvalidInformationException(
                    "O telefone é obrigatório."
            );
        }

        String telefoneFormatado = telefone.replaceAll("\\D", "");

        if (telefoneFormatado.length() < 10
                || telefoneFormatado.length() > 11) {

            throw new TriagemWithInvalidInformationException(
                    "Telefone inválido."
            );
        }

        this.telefone = telefoneFormatado;
    }

    public void setStatus(Status novoStatus) {
        if (novoStatus == null) {
            throw new TriagemWithInvalidInformationException(
                    "O status não pode ser nulo."
            );
        }

        this.status = novoStatus;
    }
    public void concluir() {
        if (this.status != Status.AGENDADA) {
            throw new TriagemWithInvalidInformationException(
                    "Apenas triagens agendadas podem ser concluídas."
            );
        }

        this.status = Status.CONCLUIDA;
    }

    public void marcarComoVencida() {
        if (this.status == Status.AGENDADA &&
                this.dataAgendada.isBefore(LocalDateTime.now())) {

            this.status = Status.ATRASADA;
        }
    }
}