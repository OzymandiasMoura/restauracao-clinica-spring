package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.*;
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

    @ManyToOne
    @JoinColumn(name = "id_modalidade")
    private Modalidade modalidade;

    @Column(nullable = false)
    private String nomePossivelAcolhido;

    @Column(nullable = false, length = 15)
    private String telefone;

    @Column(nullable = false)
    private LocalDateTime dataAgendada;

    private String observacaoEntrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Triagem(Modalidade modalidade, String nomePossivelAcolhido, String telefone, LocalDateTime dataAgendada, String observacaoEntrevista) {
        setModalidade(modalidade);
        setNomePossivelAcolhido(nomePossivelAcolhido);
        setTelefone(telefone);
        setData(dataAgendada);
        this.observacaoEntrevista = observacaoEntrevista;
        this.status = Status.PENDENTE;
    }

    public void setModalidade(Modalidade modalidade) {
        if (modalidade == null) {
            throw new TriagemWithInvalidInformationException("A triagem precisa estar vinculada a uma modalidade.");
        }

        if (!modalidade.isAtivo()) {
            throw new TriagemWithInvalidInformationException("Não é possível agendar triagem para uma modalidade inativa.");
        }

        this.modalidade = modalidade;
    }

    public void setNomePossivelAcolhido(String nomePossivelAcolhido) {
        if (nomePossivelAcolhido == null || nomePossivelAcolhido.trim().isBlank()) {
            throw new TriagemWithInvalidInformationException("O nome não pode ser nulo ou vazio.");
        }
        String nomeFormatado = nomePossivelAcolhido.trim();
        if (nomeFormatado.length() < 3){
            throw new TriagemWithInvalidInformationException("O nome do possivel acolhido deve ter mais que 3 caracteres.");

        }

        this.nomePossivelAcolhido = nomeFormatado;
    }

    public void setData(LocalDateTime novaData) {
        if (novaData == null) {
            throw new TriagemWithInvalidInformationException("A data agendada não pode ser nula.");
        }

        if (novaData.toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new TriagemWithInvalidInformationException("A data agendada não pode ser no passado.");
        }
        this.dataAgendada = novaData;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isBlank()) {
            throw new TriagemWithInvalidInformationException("O telefone é obrigatório.");
        }

        String telefoneFormatado = telefone.trim();
        if (telefoneFormatado.length() < 10) {
            throw new TriagemWithInvalidInformationException("Telefone inválido.");
        }

        this.telefone = telefoneFormatado;
    }
    public void setStatus(Status novoStatus) {
        if (novoStatus == null) {
            throw new TriagemWithInvalidInformationException("O status não pode ser nulo.");
        }
        this.status = novoStatus;
    }
}





