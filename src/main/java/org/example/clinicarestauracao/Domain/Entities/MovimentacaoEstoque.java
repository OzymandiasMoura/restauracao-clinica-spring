package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.MovimentacaoEstoque.MovimentacaoEstoqueWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.TipoMovimentacao;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "data_movimentacao", nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(name = "usuario_responsavel", nullable = false)
    private String usuarioResponsavel;

    @Column(name = "saldo_anterior", nullable = false)
    private Integer saldoAnterior;

    @Column(name = "saldo_posterior", nullable = false)
    private Integer saldoPosterior;

    @Column(nullable = false, length = 500)
    private String justificativa;

    public MovimentacaoEstoque(
            Medicamento medicamento,
            TipoMovimentacao tipo,
            Integer quantidade,
            String usuarioResponsavel,
            Integer saldoAnterior,
            Integer saldoPosterior,
            String justificativa
    ) {
        if (medicamento == null || tipo == null) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "Medicamento e tipo são obrigatórios."
            );
        }

        if (quantidade == null || quantidade <= 0) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "A quantidade deve ser maior que zero."
            );
        }

        if (usuarioResponsavel == null
                || usuarioResponsavel.isBlank()) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "O responsável é obrigatório."
            );
        }

        if (saldoAnterior == null || saldoAnterior < 0
                || saldoPosterior == null || saldoPosterior < 0) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "Os saldos devem ser válidos."
            );
        }

        if (justificativa == null
                || justificativa.isBlank()
                || justificativa.trim().length() > 500) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "A justificativa deve ter entre 1 e 500 caracteres."
            );
        }

        int saldoEsperado = tipo == TipoMovimentacao.ENTRADA
                ? Math.addExact(saldoAnterior, quantidade)
                : saldoAnterior - quantidade;

        if (saldoEsperado != saldoPosterior) {
            throw new MovimentacaoEstoqueWithInvalidInformationException(
                    "O saldo posterior não corresponde à movimentação."
            );
        }

        this.medicamento = medicamento;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.usuarioResponsavel = usuarioResponsavel.trim();
        this.saldoAnterior = saldoAnterior;
        this.saldoPosterior = saldoPosterior;
        this.justificativa = justificativa.trim();
        this.dataMovimentacao = LocalDateTime.now();
    }
}
